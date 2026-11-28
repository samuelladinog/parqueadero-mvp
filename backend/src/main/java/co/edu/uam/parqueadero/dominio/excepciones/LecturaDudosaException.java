package co.edu.uam.parqueadero.dominio.excepciones;

import co.edu.uam.parqueadero.dominio.modelo.Placa;

/**
 * La lectura automática no alcanzó el umbral de confianza de la sede: el ingreso no se registra
 * y el operador debe validar o digitar la placa (HU-03).
 */
public class LecturaDudosaException extends ExcepcionDeDominio {

    private final Placa placaLeida;
    private final double confianza;

    public LecturaDudosaException(Placa placaLeida, double confianza, double umbral) {
        super("La lectura de la placa " + placaLeida + " tiene confianza " + confianza
                + ", menor al umbral " + umbral + ": el operador debe validarla");
        this.placaLeida = placaLeida;
        this.confianza = confianza;
    }

    public Placa placaLeida() {
        return placaLeida;
    }

    public double confianza() {
        return confianza;
    }
}
