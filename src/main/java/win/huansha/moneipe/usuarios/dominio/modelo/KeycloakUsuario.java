package win.huansha.moneipe.usuarios.dominio.modelo;

public record KeycloakUsuario(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled
) {
}
