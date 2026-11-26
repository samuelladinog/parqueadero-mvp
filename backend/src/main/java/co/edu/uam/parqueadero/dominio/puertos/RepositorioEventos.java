package co.edu.uam.parqueadero.dominio.puertos;

import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;

public interface RepositorioEventos {

    /** Guarda el evento y lo devuelve con su id asignado. */
    EventoAcceso guardar(EventoAcceso evento);
}
