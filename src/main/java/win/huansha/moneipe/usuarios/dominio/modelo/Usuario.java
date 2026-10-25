package win.huansha.moneipe.usuarios.dominio.modelo;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Usuario {
    UUID id;
    String username;
    String nombres;
    String apellidos;
    String email;
    boolean activo;
    Instant createdAt;
    Instant updatedAt;
    UUID createdBy;
    UUID updatedBy;
}
