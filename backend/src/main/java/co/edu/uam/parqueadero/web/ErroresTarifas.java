package co.edu.uam.parqueadero.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Errores de las reglas de las tarifas, con el formato ProblemDetail. */
@RestControllerAdvice(assignableTypes = TarifasControlador.class)
public class ErroresTarifas {
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail reglaTarifa(IllegalArgumentException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT,
                e.getMessage());
        problema.setTitle("Tarifa no valida");
        return problema;
    }
}
