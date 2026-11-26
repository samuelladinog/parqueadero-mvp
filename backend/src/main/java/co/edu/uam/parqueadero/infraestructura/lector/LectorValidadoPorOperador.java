package co.edu.uam.parqueadero.infraestructura.lector;

import co.edu.uam.parqueadero.dominio.modelo.LecturaPlaca;
import co.edu.uam.parqueadero.dominio.puertos.LectorPlacas;

/**
 * Lector para una lectura dudosa que el operador revisó: confirma la placa que leyó la cámara o la corrige.
 * Implementa el mismo puerto que la cámara y la digitación, así que el caso de uso de ingreso no cambia.
 */
public class LectorValidadoPorOperador implements LectorPlacas {

    private final String placaLeida;
    private final String placaConfirmada;
    private final double confianzaOriginal;

    public LectorValidadoPorOperador(String placaLeida, String placaConfirmada, double confianzaOriginal) {
        this.placaLeida = placaLeida;
        this.placaConfirmada = placaConfirmada;
        this.confianzaOriginal = confianzaOriginal;
    }

    @Override
    public LecturaPlaca leer() {
        return LecturaPlaca.validadaPorOperador(placaLeida, placaConfirmada, confianzaOriginal);
    }
}
