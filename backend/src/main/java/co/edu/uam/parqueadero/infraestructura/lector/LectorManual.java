package co.edu.uam.parqueadero.infraestructura.lector;

import co.edu.uam.parqueadero.dominio.modelo.LecturaPlaca;
import co.edu.uam.parqueadero.dominio.puertos.LectorPlacas;

/**
 * Lector para la placa que digita el operador. Es intercambiable con la cámara porque implementa el mismo puerto.
 */
public class LectorManual implements LectorPlacas {

    private final String placaDigitada;

    public LectorManual(String placaDigitada) {
        this.placaDigitada = placaDigitada;
    }

    @Override
    public LecturaPlaca leer() {
        return LecturaPlaca.manual(placaDigitada);
    }
}
