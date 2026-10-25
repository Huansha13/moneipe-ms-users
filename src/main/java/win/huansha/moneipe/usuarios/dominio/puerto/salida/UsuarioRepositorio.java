package win.huansha.moneipe.usuarios.dominio.puerto.salida;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.dominio.modelo.Usuario;

import java.util.UUID;

public interface UsuarioRepositorio {
    Mono<Usuario> guardar(Usuario usuario);
    Mono<Usuario> porId(UUID id);
    Flux<Usuario> listar();
    Mono<Usuario> porUsername(String username);
    Mono<Void> desactivar(UUID id);
}
