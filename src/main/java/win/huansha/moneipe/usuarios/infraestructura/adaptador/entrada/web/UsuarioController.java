package win.huansha.moneipe.usuarios.infraestructura.adaptador.entrada.web;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.usuarios.aplicacion.servicio.UsuarioServicio;
import win.huansha.moneipe.usuarios.dominio.modelo.Usuario;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServicio servicio;

    @GetMapping
    public Flux<Usuario> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Mono<Usuario> porId(@PathVariable UUID id) {
        return servicio.porId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Usuario> crear(@RequestBody Map<String, String> body) {
        return servicio.crear(
            body.get("username"),
            body.get("email"),
            body.get("nombres"),
            body.get("apellidos"),
            body.get("password")
        );
    }

    @PutMapping("/{id}")
    public Mono<Usuario> actualizar(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        return servicio.actualizar(id, body.get("nombres"), body.get("apellidos"), body.get("email"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> desactivar(@PathVariable UUID id) {
        return servicio.desactivar(id);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cambiarPassword(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        return servicio.cambiarPassword(id, body.get("password"));
    }
}
