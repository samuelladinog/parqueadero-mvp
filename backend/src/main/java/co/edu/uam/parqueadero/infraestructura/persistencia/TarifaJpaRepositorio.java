package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaJpaRepositorio extends JpaRepository<TarifaEntidad, Long> {
    List<TarifaEntidad> findBySedeIdAndTipoVehiculoAndFranjaAndActivaTrue(
            Long sedeId, TipoVehiculo tipoVehiculo, FranjaTarifaria franja);
    List<TarifaEntidad> findBySedeIdOrderByTipoVehiculoAscFranjaAsc(Long sedeId);
}
