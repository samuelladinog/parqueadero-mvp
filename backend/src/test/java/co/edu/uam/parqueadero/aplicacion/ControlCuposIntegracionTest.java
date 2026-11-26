package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.EstadoCupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.excepciones.ParqueaderoLlenoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(
    named = "DB_URL",
    matches = ".+"
)
class ControlCuposIntegracionTest {

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
            VALUES ('Sede prueba HU04', 'Direccion prueba')
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

        jdbc.update(
            """
            INSERT INTO cupo
            (sede_id, codigo, tipo_vehiculo, estado)
            VALUES (?, 'M01', 'MOTO', 'OCUPADO')
            """,
            sedeId
        );
    }

    @Test
    void debeOcuparCupoRealDeCarro() {

        assertEquals(1, controlCupos.consultarDisponibilidad(
            sedeId, TipoVehiculo.CARRO
        ));

        Cupo cupo = controlCupos.asignarCupo(
            sedeId, TipoVehiculo.CARRO
        );

        assertEquals(EstadoCupo.OCUPADO, cupo.getEstado());

        assertEquals(0, controlCupos.consultarDisponibilidad(
            sedeId, TipoVehiculo.CARRO
        ));
    }

    @Test
    void debeRechazarMotoSinDisponibilidad() {

        ParqueaderoLlenoException excepcion = assertThrows(
            ParqueaderoLlenoException.class,
            () -> controlCupos.asignarCupo(
                sedeId, TipoVehiculo.MOTO
            )
        );

        assertTrue(excepcion.getMessage().contains("LLENO"));
    }

    @Test
    void debeLiberarCupoReal() {

        Cupo ocupado = controlCupos.asignarCupo(
            sedeId, TipoVehiculo.CARRO
        );

        Cupo liberado = controlCupos.liberarCupo(
            ocupado.getId()
        );

        assertEquals(
            EstadoCupo.DISPONIBLE,
            liberado.getEstado()
        );

        assertEquals(1, controlCupos.consultarDisponibilidad(
            sedeId, TipoVehiculo.CARRO
        ));
    }
}