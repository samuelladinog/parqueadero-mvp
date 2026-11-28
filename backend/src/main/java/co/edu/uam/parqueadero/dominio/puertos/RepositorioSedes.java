package co.edu.uam.parqueadero.dominio.puertos;

import co.edu.uam.parqueadero.dominio.modelo.Sede;

import java.util.Optional;

public interface RepositorioSedes {

    Optional<Sede> buscarPorId(Long id);
}
