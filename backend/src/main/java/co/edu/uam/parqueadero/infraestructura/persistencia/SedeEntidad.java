package co.edu.uam.parqueadero.infraestructura.persistencia;

import co.edu.uam.parqueadero.dominio.modelo.Sede;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Fila de la tabla {@code sede} (migración V1). Se mantiene separada del modelo de dominio {@link Sede}.
 */
@Entity
@Table(name = "sede")
public class SedeEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(name = "umbral_confianza", nullable = false, precision = 5, scale = 2)
    private BigDecimal umbralConfianza;

    @Column(nullable = false)
    private boolean activa;

    protected SedeEntidad() {
        // requerido por JPA
    }

    public Sede aDominio() {
        return new Sede(id, nombre, umbralConfianza.doubleValue());
    }
}
