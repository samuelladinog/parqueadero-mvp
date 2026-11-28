package co.edu.uam.parqueadero.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Representa la tabla usuario creada por Flyway en la migracion V1. */
@Entity
@Table(name = "usuario")
public class UsuarioEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "rol", nullable = false, length = 30)
    private String rol;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    protected UsuarioEntidad() {
        // Requerido por JPA.
    }

    public UsuarioEntidad(String nombre, String correo, String passwordHash, String rol, boolean activo) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
}
