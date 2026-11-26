package co.edu.uam.parqueadero.dominio.modelo;

import java.util.Objects;

public class Cupo {

    private final Long id;
    private final Long sedeId;
    private final String codigo;
    private final TipoVehiculo tipoVehiculo;
    private EstadoCupo estado;

    public Cupo(Long id, Long sedeId, String codigo,
                TipoVehiculo tipoVehiculo, EstadoCupo estado) {

        this.id = id;
        this.sedeId = Objects.requireNonNull(sedeId);
        this.codigo = Objects.requireNonNull(codigo);
        this.tipoVehiculo = Objects.requireNonNull(tipoVehiculo);
        this.estado = Objects.requireNonNull(estado);
    }

    public boolean estaDisponible() {
        return estado == EstadoCupo.DISPONIBLE;
    }

    public void ocupar() {
        if (!estaDisponible()) {
            throw new IllegalStateException(
                "El cupo no está disponible"
            );
        }

        estado = EstadoCupo.OCUPADO;
    }

    public void liberar() {
        if (estado != EstadoCupo.OCUPADO) {
            throw new IllegalStateException(
                "El cupo no está ocupado"
            );
        }

        estado = EstadoCupo.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public Long getSedeId() {
        return sedeId;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public EstadoCupo getEstado() {
        return estado;
    }
}