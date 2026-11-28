package co.edu.uam.parqueadero.web;

import co.edu.uam.parqueadero.infraestructura.seguridad.UsuarioPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Devuelve los datos publicos del usuario autenticado, nunca su password. */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionControlador {

    @GetMapping("/yo")
    public PerfilRespuesta yo(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return new PerfilRespuesta(usuario.getNombre(), usuario.getRol());
    }

    public record PerfilRespuesta(String nombre, String rol) { }
}
