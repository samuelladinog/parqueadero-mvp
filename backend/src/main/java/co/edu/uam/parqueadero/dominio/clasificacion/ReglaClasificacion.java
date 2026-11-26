package co.edu.uam.parqueadero.dominio.clasificacion;

import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

/**
 * Estrategia (patrón Strategy) que reconoce un tipo de vehículo por el formato de su placa.
 * Para soportar un tipo nuevo se agrega otra implementación; el clasificador no cambia (principio abierto/cerrado).
 */
public interface ReglaClasificacion {

    boolean aplicaA(Placa placa);

    TipoVehiculo tipo();
}
