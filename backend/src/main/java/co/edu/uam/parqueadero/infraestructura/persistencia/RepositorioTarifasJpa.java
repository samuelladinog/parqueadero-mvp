package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.Tarifa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioTarifas;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioTarifasJpa implements RepositorioTarifas {
    private final TarifaJpaRepositorio jpa;

    public RepositorioTarifasJpa(TarifaJpaRepositorio jpa) { this.jpa = jpa; }

    @Override
    public Optional<Tarifa> buscarActiva(Long sedeId, TipoVehiculo tipo, FranjaTarifaria franja) {
        var activas = jpa.findBySedeIdAndTipoVehiculoAndFranjaAndActivaTrue(sedeId, tipo, franja);
        if (activas.size() > 1) {
            throw new IllegalStateException("Hay varias tarifas activas para la misma franja y vehiculo");
        }
        return activas.stream().findFirst().map(TarifaEntidad::aDominio);
    }

    @Override
    public List<Tarifa> listarPorSede(Long sedeId) {
        return jpa.findBySedeIdOrderByTipoVehiculoAscFranjaAsc(sedeId)
                .stream().map(TarifaEntidad::aDominio).toList();
    }

    @Override
    public Tarifa guardar(Tarifa tarifa) {
        TarifaEntidad entidad = tarifa.id() == null ? new TarifaEntidad(tarifa)
                : jpa.findById(tarifa.id())
                  .orElseThrow(() -> new IllegalStateException("La tarifa existente ya no esta disponible"));
        entidad.actualizar(tarifa);
        return jpa.save(entidad).aDominio();
    }
}
