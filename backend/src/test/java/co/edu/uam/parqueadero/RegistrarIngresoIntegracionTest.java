package co.edu.uam.parqueadero;

import co.edu.uam.parqueadero.aplicacion.RegistrarIngresoServicio;
import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.infraestructura.lector.AdaptadorCamaraLpr;
import co.edu.uam.parqueadero.infraestructura.lector.ResultadoCamaraLpr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de punta a punta contra PostgreSQL real: arranca Spring, aplica las migraciones de Flyway,
 * valida que las entidades JPA coincidan con el esquema y guarda un ingreso.
 * Solo corre cuando existe la variable DB_URL (el CI la define con su servicio de PostgreSQL;
 * en local se activa levantando docker compose y exportando las variables del .env).
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class RegistrarIngresoIntegracionTest {

    @Autowired
    private RegistrarIngresoServicio registrarIngreso;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void guardaElIngresoEnLaBaseDeDatos() {
        Long sedeId = jdbc.queryForObject(
                "INSERT INTO sede (nombre, direccion, umbral_confianza) VALUES ('Sede prueba', 'Calle 1', 85.00) RETURNING id",
                Long.class);

        EventoAcceso evento = registrarIngreso.registrar(sedeId,
                new AdaptadorCamaraLpr(new ResultadoCamaraLpr("abc 123", 0.95)));

        Map<String, Object> fila = jdbc.queryForMap("SELECT * FROM evento_acceso WHERE id = ?", evento.id());
        assertThat(fila.get("placa")).isEqualTo("ABC123");
        assertThat(fila.get("tipo")).isEqualTo("INGRESO");
        assertThat(fila.get("tipo_vehiculo")).isEqualTo(TipoVehiculo.CARRO.name());
        assertThat(fila.get("lectura_manual")).isEqualTo(false);
        assertThat(fila.get("sede_id")).isEqualTo(sedeId);
    }
}
