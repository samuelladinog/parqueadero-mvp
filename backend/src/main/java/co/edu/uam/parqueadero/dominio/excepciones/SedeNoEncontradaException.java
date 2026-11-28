package co.edu.uam.parqueadero.dominio.excepciones;

public class SedeNoEncontradaException extends ExcepcionDeDominio {

    public SedeNoEncontradaException(Long sedeId) {
        super("No existe la sede " + sedeId);
    }
}
