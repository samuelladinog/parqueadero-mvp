package co.edu.uam.parqueadero.dominio.modelo;

import co.edu.uam.parqueadero.dominio.excepciones.LecturaInvalidaException;

/**
 * Lo que entrega un lector de placas: el texto leído, la confianza (0 a 100) y si fue digitado por el operador.
 * El texto llega sin normalizar; la normalización ocurre al convertirlo en {@link Placa}.
 */
public record LecturaPlaca(String textoLeido, double confianza, boolean manual) {

    public static final double CONFIANZA_MAXIMA = 100.0;

    public LecturaPlaca {
        if (Double.isNaN(confianza) || confianza < 0 || confianza > CONFIANZA_MAXIMA) {
            throw new LecturaInvalidaException("La confianza debe estar entre 0 y 100, llegó " + confianza);
        }
    }

    public static LecturaPlaca automatica(String textoLeido, double confianza) {
        return new LecturaPlaca(textoLeido, confianza, false);
    }

    /** Placa digitada o confirmada por el operador: la confianza es total. */
    public static LecturaPlaca manual(String textoLeido) {
        return new LecturaPlaca(textoLeido, CONFIANZA_MAXIMA, true);
    }
}
