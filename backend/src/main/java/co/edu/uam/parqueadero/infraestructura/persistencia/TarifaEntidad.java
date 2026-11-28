package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.Tarifa;
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
import java.time.LocalTime;

@Entity
@Table(name = "tarifa")
public class TarifaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false, length = 10)
    private TipoVehiculo tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FranjaTarifaria franja;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private boolean activa;

    protected TarifaEntidad() { }

    public TarifaEntidad(Tarifa tarifa) { actualizar(tarifa); }

    public void actualizar(Tarifa tarifa) {
        sedeId = tarifa.sedeId();
        nombre = tarifa.nombre();
        tipoVehiculo = tarifa.tipoVehiculo();
        franja = tarifa.franja();
        horaInicio = tarifa.horaInicio();
        horaFin = tarifa.horaFin();
        valor = tarifa.valor();
        activa = tarifa.activa();
    }

    public Long getId() { return id; }

    public Tarifa aDominio() {
        return new Tarifa(id, sedeId, tipoVehiculo, franja,
                nombre, horaInicio, horaFin, valor, activa);
    }
}
