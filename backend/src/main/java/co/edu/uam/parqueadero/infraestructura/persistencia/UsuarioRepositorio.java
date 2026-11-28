package co.edu.uam.parqueadero.infraestructura.persistencia;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a los usuarios almacenados en PostgreSQL. */
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntidad, Long> {
    Optional<UsuarioEntidad> findByCorreo(String correo);
}
