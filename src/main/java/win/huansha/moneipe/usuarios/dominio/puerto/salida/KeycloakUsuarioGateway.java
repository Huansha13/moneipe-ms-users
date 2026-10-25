package win.huansha.moneipe.usuarios.dominio.puerto.salida;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.dominio.modelo.KeycloakUsuario;

import java.util.UUID;

public interface KeycloakUsuarioGateway {
    Flux<KeycloakUsuario> listar();
    Mono<KeycloakUsuario> porId(UUID id);
    Mono<String> crear(KeycloakUsuario usuario, String password);
    Mono<Void> actualizar(UUID id, KeycloakUsuario usuario);
    Mono<Void> desactivar(UUID id);
    Mono<Void> cambiarPassword(UUID id, String nuevaPassword);
}
