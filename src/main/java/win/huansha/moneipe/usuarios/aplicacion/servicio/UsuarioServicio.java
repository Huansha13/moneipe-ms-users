package win.huansha.moneipe.usuarios.aplicacion.servicio;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.dominio.modelo.Usuario;
import win.huansha.moneipe.usuarios.dominio.puerto.entrada.UsuarioCasoUso;
import win.huansha.moneipe.usuarios.dominio.puerto.salida.KeycloakUsuarioGateway;
import win.huansha.moneipe.usuarios.dominio.puerto.salida.UsuarioRepositorio;
import win.huansha.moneipe.usuarios.dominio.modelo.KeycloakUsuario;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServicio implements UsuarioCasoUso {

    private final UsuarioRepositorio repo;
    private final KeycloakUsuarioGateway keycloak;

    @Override
    public Flux<Usuario> listar() {
        return repo.listar();
    }

    @Override
    public Mono<Usuario> porId(UUID id) {
        return repo.porId(id);
    }

    @Override
    public Mono<Usuario> crear(String username, String email, String nombres, String apellidos, String password) {
        KeycloakUsuario kcUser = new KeycloakUsuario(null, username, email, nombres, apellidos, true);
        return keycloak.crear(kcUser, password)
            .flatMap(kcId -> {
                UUID id = UUID.fromString(kcId);
                Usuario usuario = Usuario.builder()
                    .id(id)
                    .username(username)
                    .nombres(nombres)
                    .apellidos(apellidos)
                    .email(email)
                    .activo(true)
                    .build();
                return repo.guardar(usuario);
            });
    }

    @Override
    public Mono<Usuario> actualizar(UUID id, String nombres, String apellidos, String email) {
        return repo.porId(id)
            .flatMap(existing -> {
                Usuario actualizado = Usuario.builder()
                    .id(existing.getId())
                    .username(existing.getUsername())
                    .nombres(nombres != null ? nombres : existing.getNombres())
                    .apellidos(apellidos != null ? apellidos : existing.getApellidos())
                    .email(email != null ? email : existing.getEmail())
                    .activo(existing.isActivo())
                    .createdAt(existing.getCreatedAt())
                    .updatedAt(Instant.now())
                    .createdBy(existing.getCreatedBy())
                    .updatedBy(existing.getUpdatedBy())
                    .build();
                return repo.guardar(actualizado);
            });
    }

    @Override
    public Mono<Void> desactivar(UUID id) {
        return keycloak.desactivar(id)
            .then(repo.desactivar(id));
    }

    @Override
    public Mono<Void> cambiarPassword(UUID id, String nuevaPassword) {
        return keycloak.cambiarPassword(id, nuevaPassword);
    }
}
