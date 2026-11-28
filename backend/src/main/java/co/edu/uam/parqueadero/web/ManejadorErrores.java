package co.edu.uam.parqueadero.web;

import co.edu.uam.parqueadero.dominio.excepciones.ExcepcionDeDominio;
import co.edu.uam.parqueadero.dominio.excepciones.LecturaDudosaException;
import co.edu.uam.parqueadero.dominio.excepciones.SedeNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las reglas de negocio violadas a respuestas HTTP con un mensaje claro para la interfaz.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(SedeNoEncontradaException.class)
    public ProblemDetail sedeNoEncontrada(SedeNoEncontradaException e) {
        return problema(HttpStatus.NOT_FOUND, "Sede no encontrada", e);
    }

    /** 409: el ingreso no se registró; la interfaz debe pedir al operador validar o digitar la placa (HU-03). */
    @ExceptionHandler(LecturaDudosaException.class)
    public ProblemDetail lecturaDudosa(LecturaDudosaException e) {
        ProblemDetail problema = problema(HttpStatus.CONFLICT, "Lectura requiere validación", e);
        problema.setProperty("placaLeida", e.placaLeida().valor());
        problema.setProperty("confianza", e.confianza());
        problema.setProperty("requiereValidacion", true);
        return problema;
    }

    @ExceptionHandler(ExcepcionDeDominio.class)
    public ProblemDetail reglaDeNegocio(ExcepcionDeDominio e) {
        return problema(HttpStatus.UNPROCESSABLE_CONTENT, "Solicitud rechazada", e);
    }

    private static ProblemDetail problema(HttpStatus estado, String titulo, ExcepcionDeDominio e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, e.getMessage());
        problema.setTitle(titulo);
        return problema;
    }
}
