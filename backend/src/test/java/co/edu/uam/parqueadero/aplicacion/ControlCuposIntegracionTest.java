package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.EstadoCupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.excepciones.ParqueaderoLlenoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(
    named = "HU04_INTEGRACION",
    matches = "true"
)
class ControlCuposIntegracionTest {

    @Autowired
    private ControlCupos controlCupos;

    @Test
    void debeOcuparCupoRealDeCarro() {

        assertEquals(1, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));

        Cupo cupo = controlCupos.asignarCupo(
            1L, TipoVehiculo.CARRO
        );

        assertEquals(EstadoCupo.OCUPADO, cupo.getEstado());

        assertEquals(0, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));
    }

    @Test
    void debeRechazarMotoSinDisponibilidad() {

        ParqueaderoLlenoException excepcion = assertThrows(
            ParqueaderoLlenoException.class,
            () -> controlCupos.asignarCupo(
                1L, TipoVehiculo.MOTO
            )
        );

        assertTrue(excepcion.getMessage().contains("LLENO"));
    }

    @Test
    void debeLiberarCupoReal() {

        Cupo ocupado = controlCupos.asignarCupo(
            1L, TipoVehiculo.CARRO
        );

        Cupo liberado = controlCupos.liberarCupo(
            ocupado.getId()
        );

        assertEquals(
            EstadoCupo.DISPONIBLE,
            liberado.getEstado()
        );

        assertEquals(1, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));
    }
}