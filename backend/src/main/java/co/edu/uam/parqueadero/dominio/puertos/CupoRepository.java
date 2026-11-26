package co.edu.uam.parqueadero.dominio.puertos;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

import java.util.Optional;

public interface CupoRepository {

    Optional<Cupo> buscarDisponible(
        Long sedeId, TipoVehiculo tipoVehiculo
    );

    Optional<Cupo> buscarPorId(Long id);

    long contarDisponibles(
        Long sedeId, TipoVehiculo tipoVehiculo
    );

    Cupo guardar(Cupo cupo);
}