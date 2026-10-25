package win.huansha.moneipe.usuarios.infraestructura.adaptador.salida.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.dominio.modelo.KeycloakUsuario;
import win.huansha.moneipe.usuarios.dominio.puerto.salida.KeycloakUsuarioGateway;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminClient implements KeycloakUsuarioGateway {

    private final WebClient webClient;

    @Value("${keycloak.admin.base-url}")
    private String baseUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private volatile String cachedToken;
    private volatile long tokenExpiry;

    private Mono<String> obtenerToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiry) {
            return Mono.just(cachedToken);
        }
        return webClient.post()
            .uri(baseUrl + "/realms/" + realm + "/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
            .retrieve()
            .bodyToMono(Map.class)
            .map(resp -> {
                String token = (String) resp.get("access_token");
                int expiresIn = (int) resp.getOrDefault("expires_in", 300);
                cachedToken = token;
                tokenExpiry = System.currentTimeMillis() + (expiresIn - 30) * 1000L;
                return token;
            });
    }

    private String adminUrl() {
        return baseUrl + "/admin/realms/" + realm;
    }

    @Override
    public Flux<KeycloakUsuario> listar() {
        return obtenerToken()
            .flatMapMany(token -> webClient.get()
                .uri(adminUrl() + "/users?max=1000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(this::toKeycloakUsuario));
    }

    @Override
    public Mono<KeycloakUsuario> porId(UUID id) {
        return obtenerToken()
            .flatMap(token -> webClient.get()
                .uri(adminUrl() + "/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::toKeycloakUsuario));
    }

    @Override
    public Mono<String> crear(KeycloakUsuario usuario, String password) {
        Map<String, Object> payload = Map.of(
            "username", usuario.username(),
            "email", usuario.email() != null ? usuario.email() : "",
            "firstName", usuario.firstName() != null ? usuario.firstName() : "",
            "lastName", usuario.lastName() != null ? usuario.lastName() : "",
            "enabled", usuario.enabled(),
            "credentials", List.of(Map.of(
                "type", "password",
                "value", password,
                "temporary", false
            ))
        );
        return obtenerToken()
            .flatMap(token -> webClient.post()
                .uri(adminUrl() + "/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> {
                    String location = resp.getHeaders().getFirst("Location");
                    if (location != null) {
                        return location.substring(location.lastIndexOf('/') + 1);
                    }
                    return null;
                }));
    }

    @Override
    public Mono<Void> actualizar(UUID id, KeycloakUsuario usuario) {
        Map<String, Object> payload = Map.of(
            "firstName", usuario.firstName() != null ? usuario.firstName() : "",
            "lastName", usuario.lastName() != null ? usuario.lastName() : "",
            "email", usuario.email() != null ? usuario.email() : "",
            "enabled", usuario.enabled()
        );
        return obtenerToken()
            .flatMap(token -> webClient.put()
                .uri(adminUrl() + "/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .then());
    }

    @Override
    public Mono<Void> desactivar(UUID id) {
        return obtenerToken()
            .flatMap(token -> webClient.put()
                .uri(adminUrl() + "/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("enabled", false))
                .retrieve()
                .toBodilessEntity()
                .then());
    }

    @Override
    public Mono<Void> cambiarPassword(UUID id, String nuevaPassword) {
        Map<String, Object> payload = Map.of(
            "type", "password",
            "value", nuevaPassword,
            "temporary", false
        );
        return obtenerToken()
            .flatMap(token -> webClient.put()
                .uri(adminUrl() + "/users/" + id + "/reset-password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .then());
    }

    private KeycloakUsuario toKeycloakUsuario(Map<String, Object> m) {
        String id = (String) m.get("id");
        return new KeycloakUsuario(
            id,
            (String) m.get("username"),
            (String) m.get("email"),
            (String) m.get("firstName"),
            (String) m.get("lastName"),
            Boolean.TRUE.equals(m.get("enabled"))
        );
    }
}
