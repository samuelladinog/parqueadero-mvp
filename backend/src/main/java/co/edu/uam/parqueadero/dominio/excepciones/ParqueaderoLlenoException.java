package co.edu.uam.parqueadero.dominio.excepciones;

import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

public class ParqueaderoLlenoException extends ExcepcionDeDominio {

    public ParqueaderoLlenoException(TipoVehiculo tipoVehiculo) {
        super("LLENO: No hay cupos disponibles para " + tipoVehiculo);
    }
}