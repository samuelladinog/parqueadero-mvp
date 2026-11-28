package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioEventos;
import org.springframework.stereotype.Repository;

/**
 * Implementa el puerto {@link RepositorioEventos} con Spring Data JPA.
 */
@Repository
public class RepositorioEventosJpa implements RepositorioEventos {

    private final EventoAccesoJpaRepositorio jpa;

    public RepositorioEventosJpa(EventoAccesoJpaRepositorio jpa) {
        this.jpa = jpa;
    }

    @Override
    public EventoAcceso guardar(EventoAcceso evento) {
        return jpa.save(EventoAccesoEntidad.desde(evento)).aDominio();
    }
}
