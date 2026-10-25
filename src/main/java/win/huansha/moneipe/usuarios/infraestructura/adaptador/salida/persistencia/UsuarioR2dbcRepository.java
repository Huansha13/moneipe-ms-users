package win.huansha.moneipe.usuarios.infraestructura.adaptador.salida.persistencia;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface UsuarioR2dbcRepository extends ReactiveCrudRepository<UsuarioEntity, UUID> {
}
