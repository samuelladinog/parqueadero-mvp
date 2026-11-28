package co.edu.uam.parqueadero.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

interface SedeJpaRepositorio extends JpaRepository<SedeEntidad, Long> {
}
