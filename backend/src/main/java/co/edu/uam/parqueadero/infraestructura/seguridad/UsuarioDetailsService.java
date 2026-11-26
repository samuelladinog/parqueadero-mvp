package co.edu.uam.parqueadero.infraestructura.seguridad;

import co.edu.uam.parqueadero.infraestructura.persistencia.UsuarioRepositorio;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Consulta el usuario por correo. Spring Security valida su password con BCrypt. */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepositorio repositorio;

    public UsuarioDetailsService(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return repositorio.findByCorreo(correo)
                .map(UsuarioPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales invalidas"));
    }
}
