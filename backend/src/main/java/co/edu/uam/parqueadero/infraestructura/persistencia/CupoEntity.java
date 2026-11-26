package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.EstadoCupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

import jakarta.persistence.*;

@Entity
@Table(name = "cupo")
public class CupoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false, length = 10)
    private TipoVehiculo tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCupo estado;

    protected CupoEntity() {
    }

    public CupoEntity(Cupo cupo) {
        this.id = cupo.getId();
        this.sedeId = cupo.getSedeId();
        this.codigo = cupo.getCodigo();
        this.tipoVehiculo = cupo.getTipoVehiculo();
        this.estado = cupo.getEstado();
    }

    public Cupo aDominio() {
        return new Cupo(
            id,
            sedeId,
            codigo,
            tipoVehiculo,
            estado
        );
    }
}