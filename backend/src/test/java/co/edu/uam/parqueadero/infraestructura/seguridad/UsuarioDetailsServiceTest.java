package co.edu.uam.parqueadero.infraestructura.seguridad;

import co.edu.uam.parqueadero.infraestructura.persistencia.UsuarioEntidad;
import co.edu.uam.parqueadero.infraestructura.persistencia.UsuarioRepositorio;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioDetailsServiceTest {

    @Test
    void cargaElRolYElNombreDeUnUsuarioActivo() {
        UsuarioRepositorio repositorio = mock(UsuarioRepositorio.class);
        when(repositorio.findByCorreo("admin@ejemplo.test"))
                .thenReturn(Optional.of(new UsuarioEntidad(
                        "Administradora", "admin@ejemplo.test", "hash", "ADMIN", true)));

        var usuario = new UsuarioDetailsService(repositorio).loadUserByUsername("admin@ejemplo.test");

        assertEquals("admin@ejemplo.test", usuario.getUsername());
        assertEquals("ROLE_ADMIN", usuario.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void marcaComoDeshabilitadoUnUsuarioInactivo() {
        UsuarioRepositorio repositorio = mock(UsuarioRepositorio.class);
        when(repositorio.findByCorreo("inactivo@ejemplo.test"))
                .thenReturn(Optional.of(new UsuarioEntidad(
                        "Inactivo", "inactivo@ejemplo.test", "hash", "OPERADOR", false)));

        var usuario = new UsuarioDetailsService(repositorio).loadUserByUsername("inactivo@ejemplo.test");
        assertFalse(usuario.isEnabled());
    }

    @Test
    void rechazaUnCorreoQueNoExiste() {
        UsuarioRepositorio repositorio = mock(UsuarioRepositorio.class);
        when(repositorio.findByCorreo("desconocido@ejemplo.test")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> new UsuarioDetailsService(repositorio).loadUserByUsername("desconocido@ejemplo.test"));
    }
}
