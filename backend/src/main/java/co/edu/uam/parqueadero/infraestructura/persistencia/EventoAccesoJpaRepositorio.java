package co.edu.uam.parqueadero.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

interface EventoAccesoJpaRepositorio extends JpaRepository<EventoAccesoEntidad, Long> {
}
