package win.huansha.moneipe.usuarios.dominio.puerto.entrada;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.dominio.modelo.Usuario;

import java.util.UUID;

public interface UsuarioCasoUso {
    Flux<Usuario> listar();
    Mono<Usuario> porId(UUID id);
    Mono<Usuario> crear(String username, String email, String nombres, String apellidos, String password);
    Mono<Usuario> actualizar(UUID id, String nombres, String apellidos, String email);
    Mono<Void> desactivar(UUID id);
    Mono<Void> cambiarPassword(UUID id, String nuevaPassword);
}
