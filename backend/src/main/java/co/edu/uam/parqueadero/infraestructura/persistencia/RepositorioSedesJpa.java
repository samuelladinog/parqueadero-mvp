package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.Sede;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioSedes;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementa el puerto {@link RepositorioSedes} con Spring Data JPA.
 */
@Repository
public class RepositorioSedesJpa implements RepositorioSedes {

    private final SedeJpaRepositorio jpa;

    public RepositorioSedesJpa(SedeJpaRepositorio jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Sede> buscarPorId(Long id) {
        return jpa.findById(id).map(SedeEntidad::aDominio);
    }
}
