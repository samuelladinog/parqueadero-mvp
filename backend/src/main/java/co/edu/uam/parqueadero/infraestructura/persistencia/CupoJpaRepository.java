package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.EstadoCupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CupoJpaRepository
        extends JpaRepository<CupoEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CupoEntity>
        findFirstBySedeIdAndTipoVehiculoAndEstadoOrderByIdAsc(
            Long sedeId,
            TipoVehiculo tipoVehiculo,
            EstadoCupo estado
        );

    long countBySedeIdAndTipoVehiculoAndEstado(
        Long sedeId,
        TipoVehiculo tipoVehiculo,
        EstadoCupo estado
    );

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CupoEntity> findById(Long id);
}