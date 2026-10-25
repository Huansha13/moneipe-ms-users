package win.huansha.moneipe.usuarios.infraestructura.adaptador.entrada.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ManejadorExcepciones {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ManejadorExcepciones.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Map<String, String>> error(Exception ex) {
        log.error("Error no manejado: {}", ex.getMessage(), ex);
        return Mono.just(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Error interno"));
    }
}
