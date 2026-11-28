package co.edu.uam.parqueadero.dominio.puertos;

import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.Tarifa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import java.util.List;
import java.util.Optional;

public interface RepositorioTarifas {
    Optional<Tarifa> buscarActiva(Long sedeId, TipoVehiculo tipo, FranjaTarifaria franja);
    List<Tarifa> listarPorSede(Long sedeId);
    Tarifa guardar(Tarifa tarifa);
}
