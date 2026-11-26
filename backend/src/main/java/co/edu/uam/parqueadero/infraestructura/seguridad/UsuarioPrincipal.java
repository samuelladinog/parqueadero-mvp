package co.edu.uam.parqueadero.infraestructura.seguridad;

import co.edu.uam.parqueadero.infraestructura.persistencia.UsuarioEntidad;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Datos que Spring Security conserva durante la peticion autenticada. */
public class UsuarioPrincipal implements UserDetails {

    private final String nombre;
    private final String correo;
    private final String passwordHash;
    private final String rol;
    private final boolean activo;

    public UsuarioPrincipal(UsuarioEntidad usuario) {
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.passwordHash = usuario.getPasswordHash();
        this.rol = usuario.getRol();
        this.activo = usuario.isActivo();
    }

    public String getNombre() { return nombre; }
    public String getRol() { return rol; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return correo; }

    @Override
    public boolean isEnabled() { return activo; }
}
