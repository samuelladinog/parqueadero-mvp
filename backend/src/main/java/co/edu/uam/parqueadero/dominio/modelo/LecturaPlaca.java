package co.edu.uam.parqueadero.dominio.modelo;

import co.edu.uam.parqueadero.dominio.excepciones.LecturaInvalidaException;

/**
 * Lo que entrega un lector de placas: el texto leído, la confianza (0 a 100), si fue validado por el operador
 * y una observación para auditar las acciones manuales.
 * El texto llega sin normalizar; la normalización ocurre al convertirlo en {@link Placa}.
 */
public record LecturaPlaca(String textoLeido, double confianza, boolean manual, String observacion) {

    public static final double CONFIANZA_MAXIMA = 100.0;

    public LecturaPlaca {
        if (Double.isNaN(confianza) || confianza < 0 || confianza > CONFIANZA_MAXIMA) {
            throw new LecturaInvalidaException("La confianza debe estar entre 0 y 100, llegó " + confianza);
        }
    }

    public static LecturaPlaca automatica(String textoLeido, double confianza) {
        return new LecturaPlaca(textoLeido, confianza, false, null);
    }

    /** Placa digitada por el operador sin lectura previa de la cámara: la confianza es total. */
    public static LecturaPlaca manual(String textoLeido) {
        return new LecturaPlaca(textoLeido, CONFIANZA_MAXIMA, true, "Placa digitada por el operador");
    }

    /**
     * El operador revisó una lectura dudosa (HU-03): confirma la placa leída o la corrige.
     * Se conserva la confianza original de la cámara para la auditoría.
     */
    public static LecturaPlaca validadaPorOperador(String placaLeida, String placaConfirmada, double confianzaOriginal) {
        String leida = Placa.normalizar(placaLeida);
        String confirmada = Placa.normalizar(placaConfirmada);
        String observacion = leida.equals(confirmada)
                ? "Lectura confirmada por el operador"
                : "Lectura corregida por el operador: la cámara leyó " + (leida.isEmpty() ? "(vacío)" : leida);
        return new LecturaPlaca(placaConfirmada, confianzaOriginal, true, observacion);
    }
}
