package win.huansha.moneipe.usuarios.infraestructura.adaptador.salida.persistencia;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.dominio.modelo.Usuario;
import win.huansha.moneipe.usuarios.dominio.puerto.salida.UsuarioRepositorio;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioAdaptador implements UsuarioRepositorio {

    private final UsuarioR2dbcRepository repo;

    @Override
    public Mono<Usuario> guardar(Usuario usuario) {
        UsuarioEntity e = new UsuarioEntity();
        e.setId(usuario.getId());
        e.setUsername(usuario.getUsername());
        e.setNombres(usuario.getNombres());
        e.setApellidos(usuario.getApellidos());
        e.setEmail(usuario.getEmail());
        e.setActivo(usuario.isActivo());
        e.setCreatedAt(usuario.getCreatedAt());
        e.setUpdatedAt(usuario.getUpdatedAt());
        e.setCreatedBy(usuario.getCreatedBy());
        e.setUpdatedBy(usuario.getUpdatedBy());
        return repo.save(e).map(this::toDomain);
    }

    @Override
    public Mono<Usuario> porId(UUID id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Usuario> listar() {
        return repo.findAll()
            .sort((a, b) -> {
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            })
            .map(this::toDomain);
    }

    @Override
    public Mono<Usuario> porUsername(String username) {
        return repo.findAll()
            .filter(e -> username.equals(e.getUsername()))
            .next()
            .map(this::toDomain);
    }

    @Override
    public Mono<Void> desactivar(UUID id) {
        return repo.findById(id)
            .flatMap(e -> {
                e.setActivo(false);
                return repo.save(e);
            })
            .then();
    }

    private Usuario toDomain(UsuarioEntity e) {
        return Usuario.builder()
            .id(e.getId())
            .username(e.getUsername())
            .nombres(e.getNombres())
            .apellidos(e.getApellidos())
            .email(e.getEmail())
            .activo(e.isActivo())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .createdBy(e.getCreatedBy())
            .updatedBy(e.getUpdatedBy())
            .build();
    }
}
