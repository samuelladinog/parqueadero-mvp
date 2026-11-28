package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.TipoEvento;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Fila de la tabla {@code evento_acceso} (migración V1). Las columnas de cupo y usuario
 * se mapearán cuando las historias que las usan (HU-04, HU-06) lo necesiten.
 */
@Entity
@Table(name = "evento_acceso")
public class EventoAccesoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(nullable = false, length = 15)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", length = 10)
    private TipoVehiculo tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoEvento tipo;

    @Column(precision = 5, scale = 2)
    private BigDecimal confianza;

    @Column(name = "lectura_manual", nullable = false)
    private boolean lecturaManual;

    @Column(name = "acceso_autorizado", nullable = false)
    private boolean accesoAutorizado;

    /** Columna {@code motivo} de la V1: guarda la observación de las acciones manuales para auditoría. */
    @Column(name = "motivo", length = 255)
    private String observacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    protected EventoAccesoEntidad() {
        // requerido por JPA
    }

    static EventoAccesoEntidad desde(EventoAcceso evento) {
        EventoAccesoEntidad entidad = new EventoAccesoEntidad();
        entidad.id = evento.id();
        entidad.sedeId = evento.sedeId();
        entidad.placa = evento.placa().valor();
        entidad.tipoVehiculo = evento.tipoVehiculo();
        entidad.tipo = evento.tipo();
        entidad.confianza = BigDecimal.valueOf(evento.confianza()).setScale(2, RoundingMode.HALF_UP);
        entidad.lecturaManual = evento.lecturaManual();
        entidad.accesoAutorizado = true;
        entidad.observacion = evento.observacion();
        entidad.fechaHora = evento.fechaHora();
        return entidad;
    }

    EventoAcceso aDominio() {
        return new EventoAcceso(id, sedeId, Placa.de(placa), tipoVehiculo, tipo,
                confianza.doubleValue(), lecturaManual, observacion, fechaHora);
    }
}
