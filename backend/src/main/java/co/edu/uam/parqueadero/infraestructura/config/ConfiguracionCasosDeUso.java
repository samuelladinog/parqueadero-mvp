package co.edu.uam.parqueadero.infraestructura.config;

import co.edu.uam.parqueadero.aplicacion.RegistrarIngresoServicio;
import co.edu.uam.parqueadero.dominio.clasificacion.ClasificadorVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioEventos;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioSedes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Arma los casos de uso. El dominio y la aplicación no conocen Spring: aquí se conectan con los adaptadores.
 */
@Configuration
public class ConfiguracionCasosDeUso {

    /** Reloj de la operación. Se inyecta para que las pruebas puedan fijar la hora (día operativo, franjas). */
    @Bean
    public Clock reloj(@Value("${parqueadero.zona-horaria:America/Bogota}") String zonaHoraria) {
        return Clock.system(ZoneId.of(zonaHoraria));
    }

    @Bean
    public ClasificadorVehiculo clasificadorVehiculo() {
        return ClasificadorVehiculo.conReglasColombianas();
    }

    @Bean
    public RegistrarIngresoServicio registrarIngresoServicio(ClasificadorVehiculo clasificador,
                                                             RepositorioSedes sedes,
                                                             RepositorioEventos eventos,
                                                             Clock reloj) {
        return new RegistrarIngresoServicio(clasificador, sedes, eventos, reloj);
    }
}
