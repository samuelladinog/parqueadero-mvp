package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.excepciones.ParqueaderoLlenoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnabledIfEnvironmentVariable(
    named = "HU04_INTEGRACION",
    matches = "true"
)
class ControlCuposConcurrenciaTest {

    @Autowired
    private ControlCupos controlCupos;

    @Test
    void soloUnVehiculoDebeOcuparElUltimoCupo()
            throws Exception {

        assertEquals(1, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));

        ExecutorService executor =
            Executors.newFixedThreadPool(2);

        CountDownLatch inicio = new CountDownLatch(1);

        Callable<Boolean> ingreso = () -> {
            inicio.await();

            try {
                controlCupos.asignarCupo(
                    1L, TipoVehiculo.CARRO
                );
                return true;

            } catch (ParqueaderoLlenoException e) {
                return false;
            }
        };

        try {
            Future<Boolean> primero = executor.submit(ingreso);
            Future<Boolean> segundo = executor.submit(ingreso);

            inicio.countDown();

            boolean resultado1 = primero.get(
                15, TimeUnit.SECONDS
            );

            boolean resultado2 = segundo.get(
                15, TimeUnit.SECONDS
            );

            assertNotEquals(resultado1, resultado2);

            assertEquals(0,
                controlCupos.consultarDisponibilidad(
                    1L, TipoVehiculo.CARRO
                )
            );

        } finally {
            executor.shutdownNow();

            if (executor.awaitTermination(20, TimeUnit.SECONDS)
                    && controlCupos.consultarDisponibilidad(
                        1L, TipoVehiculo.CARRO
                    ) == 0) {

                controlCupos.liberarCupo(1L);
            }
        }
    }
}