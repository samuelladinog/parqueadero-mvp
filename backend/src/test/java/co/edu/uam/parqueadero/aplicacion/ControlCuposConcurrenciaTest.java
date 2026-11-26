package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.excepciones.ParqueaderoLlenoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnabledIfEnvironmentVariable(
    named = "DB_URL",
    matches = ".+"
)
class ControlCuposConcurrenciaTest {

    @Autowired
    private ControlCupos controlCupos;

    @Autowired
    private JdbcTemplate jdbc;

    private Long sedeId;

    @BeforeEach
    void prepararDatos() {

        sedeId = jdbc.queryForObject(
            """
            INSERT INTO sede (nombre, direccion)
            VALUES ('Sede concurrencia HU04', 'Direccion prueba')
            RETURNING id
            """,
            Long.class
        );

        jdbc.update(
            """
            INSERT INTO cupo
            (sede_id, codigo, tipo_vehiculo, estado)
            VALUES (?, 'C01', 'CARRO', 'DISPONIBLE')
            """,
            sedeId
        );
    }

    @AfterEach
    void limpiarDatos() {

        if (sedeId != null) {
            jdbc.update(
                "DELETE FROM cupo WHERE sede_id = ?",
                sedeId
            );

            jdbc.update(
                "DELETE FROM sede WHERE id = ?",
                sedeId
            );
        }
    }

    @Test
    void soloUnVehiculoDebeOcuparElUltimoCupo()
            throws Exception {

        assertEquals(1, controlCupos.consultarDisponibilidad(
            sedeId, TipoVehiculo.CARRO
        ));

        ExecutorService executor =
            Executors.newFixedThreadPool(2);

        CountDownLatch inicio = new CountDownLatch(1);

        Callable<Boolean> ingreso = () -> {

            inicio.await();

            try {
                controlCupos.asignarCupo(
                    sedeId, TipoVehiculo.CARRO
                );

                return true;

            } catch (ParqueaderoLlenoException e) {
                return false;
            }
        };

        try {

            Future<Boolean> primero =
                executor.submit(ingreso);

            Future<Boolean> segundo =
                executor.submit(ingreso);

            inicio.countDown();

            boolean resultado1 =
                primero.get(15, TimeUnit.SECONDS);

            boolean resultado2 =
                segundo.get(15, TimeUnit.SECONDS);

            assertNotEquals(resultado1, resultado2);

            assertEquals(0,
                controlCupos.consultarDisponibilidad(
                    sedeId, TipoVehiculo.CARRO
                )
            );

        } finally {

            executor.shutdownNow();

            assertTrue(
                executor.awaitTermination(
                    20, TimeUnit.SECONDS
                )
            );
        }
    }
}