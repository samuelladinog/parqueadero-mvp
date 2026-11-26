package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.EstadoCupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.CupoRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CupoRepositoryAdapter implements CupoRepository {

    private final CupoJpaRepository jpaRepository;

    public CupoRepositoryAdapter(CupoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Cupo> buscarDisponible(
            Long sedeId, TipoVehiculo tipoVehiculo) {

        return jpaRepository
            .findFirstBySedeIdAndTipoVehiculoAndEstadoOrderByIdAsc(
                sedeId,
                tipoVehiculo,
                EstadoCupo.DISPONIBLE
            )
            .map(CupoEntity::aDominio);
    }

    @Override
    public Optional<Cupo> buscarPorId(Long id) {
        return jpaRepository.findById(id)
            .map(CupoEntity::aDominio);
    }

    @Override
    public long contarDisponibles(
            Long sedeId, TipoVehiculo tipoVehiculo) {

        return jpaRepository.countBySedeIdAndTipoVehiculoAndEstado(
            sedeId,
            tipoVehiculo,
            EstadoCupo.DISPONIBLE
        );
    }

    @Override
    public Cupo guardar(Cupo cupo) {
        CupoEntity entidad = new CupoEntity(cupo);

        return jpaRepository.save(entidad).aDominio();
    }
}