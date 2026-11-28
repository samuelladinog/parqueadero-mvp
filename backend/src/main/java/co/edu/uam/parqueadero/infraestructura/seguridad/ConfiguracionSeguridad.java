package co.edu.uam.parqueadero.infraestructura.seguridad;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** HTTP Basic sin sesion: ADMIN administra; OPERADOR y ADMIN registran ingresos. */
@Configuration
public class ConfiguracionSeguridad {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider proveedorAutenticacion(
            UsuarioDetailsService servicioUsuarios, PasswordEncoder codificador) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(servicioUsuarios);
        proveedor.setPasswordEncoder(codificador);
        return proveedor;
    }

    @Bean
    public SecurityFilterChain filtroSeguridad(
            HttpSecurity http, AuthenticationProvider proveedorAutenticacion, ObjectMapper json) throws Exception {
        return http
                .authenticationProvider(proveedorAutenticacion)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/api/admin", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/sedes/*/ingresos", "/api/sedes/*/ingresos/**")
                            .hasAnyRole("OPERADOR", "ADMIN")
                        .requestMatchers("/api/auth/yo").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((peticion, respuesta, excepcion) ->
                                respuestaProblema(respuesta, HttpStatus.UNAUTHORIZED, "No autenticado", "Credenciales invalidas o ausentes", json))
                        .accessDeniedHandler((peticion, respuesta, excepcion) ->
                                respuestaProblema(respuesta, HttpStatus.FORBIDDEN, "Acceso denegado", "No tiene permisos para acceder a este recurso", json))
                )
                .httpBasic(basic -> basic.authenticationEntryPoint((peticion, respuesta, excepcion) ->
                        respuestaProblema(respuesta, HttpStatus.UNAUTHORIZED, "No autenticado", "Credenciales invalidas o ausentes", json)))
                .build();
    }

    // CORS para frontends locales durante el desarrollo. En produccion deben definirse origenes concretos.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        cors.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource origenes = new UrlBasedCorsConfigurationSource();
        origenes.registerCorsConfiguration("/**", cors);
        return origenes;
    }

    private static void respuestaProblema(HttpServletResponse respuesta, HttpStatus estado,
                                        String titulo, String detalle, ObjectMapper json)
            throws IOException {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        respuesta.setStatus(estado.value());
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        if (estado == HttpStatus.UNAUTHORIZED) {
            respuesta.setHeader("WWW-Authenticate", "Basic realm=\"Parqueadero\"");
        }
        json.writeValue(respuesta.getOutputStream(), problema);
    }
}
