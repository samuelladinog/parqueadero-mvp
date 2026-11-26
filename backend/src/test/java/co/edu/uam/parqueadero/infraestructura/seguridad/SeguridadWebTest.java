package co.edu.uam.parqueadero.infraestructura.seguridad;

import co.edu.uam.parqueadero.infraestructura.persistencia.UsuarioEntidad;
import co.edu.uam.parqueadero.web.AutenticacionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AutenticacionController.class)
@Import({ConfiguracionSeguridad.class, SeguridadWebTest.RutasPrueba.class})
class SeguridadWebTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UsuarioDetailsService servicioUsuarios;

    @BeforeEach
    void prepararUsuarios() {
        when(servicioUsuarios.loadUserByUsername("operador@ejemplo.test"))
                .thenReturn(new UsuarioPrincipal(new UsuarioEntidad(
                        "Operadora", "operador@ejemplo.test", passwordEncoder.encode("clave-operador"),
                        "OPERADOR", true)));
        when(servicioUsuarios.loadUserByUsername("admin@ejemplo.test"))
                .thenReturn(new UsuarioPrincipal(new UsuarioEntidad(
                        "Administradora", "admin@ejemplo.test", passwordEncoder.encode("clave-admin"),
                        "ADMIN", true)));
        when(servicioUsuarios.loadUserByUsername("inactivo@ejemplo.test"))
                .thenReturn(new UsuarioPrincipal(new UsuarioEntidad(
                        "Inactivo", "inactivo@ejemplo.test", passwordEncoder.encode("clave-inactivo"),
                        "OPERADOR", false)));
    }

    @Test
    void operadorPuedeConsultarSuNombreYRol() throws Exception {
        mvc.perform(get("/api/auth/yo").with(httpBasic("operador@ejemplo.test", "clave-operador")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Operadora"))
                .andExpect(jsonPath("$.rol").value("OPERADOR"));
    }

    @Test
    void operadorPuedeRegistrarIngresos() throws Exception {
        mvc.perform(get("/api/sedes/1/ingresos/prueba")
                        .with(httpBasic("operador@ejemplo.test", "clave-operador")))
                .andExpect(status().isOk());
    }

    @Test
    void adminPuedeRegistrarIngresos() throws Exception {
        mvc.perform(get("/api/sedes/1/ingresos/prueba")
                        .with(httpBasic("admin@ejemplo.test", "clave-admin")))
                .andExpect(status().isOk());
    }

    @Test
    void operadorNoPuedeEntrarAAdministracion() throws Exception {
        mvc.perform(get("/api/admin/prueba")
                        .with(httpBasic("operador@ejemplo.test", "clave-operador")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeEntrarAAdministracion() throws Exception {
        mvc.perform(get("/api/admin/prueba")
                        .with(httpBasic("admin@ejemplo.test", "clave-admin")))
                .andExpect(status().isOk());
    }

    @Test
    void accesoSinCredencialesDevuelve401() throws Exception {
        mvc.perform(get("/api/auth/yo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void claveIncorrectaDevuelve401() throws Exception {
        mvc.perform(get("/api/auth/yo").with(httpBasic("operador@ejemplo.test", "otra-clave")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioInactivoDevuelve401() throws Exception {
        mvc.perform(get("/api/auth/yo").with(httpBasic("inactivo@ejemplo.test", "clave-inactivo")))
                .andExpect(status().isUnauthorized());
    }

    @RestController
    static class RutasPrueba {
        @GetMapping("/api/sedes/1/ingresos/prueba")
        String ingreso() { return "ok"; }

        @GetMapping("/api/admin/prueba")
        String administracion() { return "ok"; }
    }
}
