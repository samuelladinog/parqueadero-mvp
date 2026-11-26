package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.EstadoCupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.CupoRepository;
import co.edu.uam.parqueadero.dominio.excepciones.ParqueaderoLlenoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ControlCuposTest {

    private ControlCupos controlCupos;
    private RepositorioEnMemoria repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        controlCupos = new ControlCupos(repositorio);
    }

    @Test
    void debeAsignarUltimoCupoDeCarro() {

        repositorio.guardar(new Cupo(
            1L, 1L, "C01",
            TipoVehiculo.CARRO,
            EstadoCupo.DISPONIBLE
        ));

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
    void debeRechazarMotoCuandoNoHayCupos() {

        ParqueaderoLlenoException excepcion = assertThrows(
            ParqueaderoLlenoException.class,
            () -> controlCupos.asignarCupo(
                1L, TipoVehiculo.MOTO
            )
        );

        assertTrue(excepcion.getMessage().contains("LLENO"));
    }

    @Test
    void debeLiberarCupoAlSalir() {

        repositorio.guardar(new Cupo(
            1L, 1L, "C01",
            TipoVehiculo.CARRO,
            EstadoCupo.OCUPADO
        ));

        Cupo cupo = controlCupos.liberarCupo(1L);

        assertEquals(EstadoCupo.DISPONIBLE, cupo.getEstado());

        assertEquals(1, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));
    }

    @Test
    void debeMantenerCuposIndependientesPorTipo() {

        repositorio.guardar(new Cupo(
            1L, 1L, "C01",
            TipoVehiculo.CARRO,
            EstadoCupo.DISPONIBLE
        ));

        repositorio.guardar(new Cupo(
            2L, 1L, "M01",
            TipoVehiculo.MOTO,
            EstadoCupo.DISPONIBLE
        ));

        controlCupos.asignarCupo(1L, TipoVehiculo.CARRO);

        assertEquals(0, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));

        assertEquals(1, controlCupos.consultarDisponibilidad(
            1L, TipoVehiculo.MOTO
        ));
    }

    @Test
    void noDebeAsignarCuposInactivos() {

        repositorio.guardar(new Cupo(
            1L, 1L, "M01",
            TipoVehiculo.MOTO,
            EstadoCupo.INACTIVO
        ));

        assertThrows(
            ParqueaderoLlenoException.class,
            () -> controlCupos.asignarCupo(
                1L, TipoVehiculo.MOTO
            )
        );
    }

    @Test
    void debeMantenerCuposIndependientesPorSede() {

        repositorio.guardar(new Cupo(
            1L, 2L, "C01",
            TipoVehiculo.CARRO,
            EstadoCupo.DISPONIBLE
        ));

        assertFalse(controlCupos.hayDisponibilidad(
            1L, TipoVehiculo.CARRO
        ));

        assertTrue(controlCupos.hayDisponibilidad(
            2L, TipoVehiculo.CARRO
        ));
    }

    @Test
    void noDebeAsignarDosVecesElMismoCupo() {

        repositorio.guardar(new Cupo(
            1L, 1L, "C01",
            TipoVehiculo.CARRO,
            EstadoCupo.DISPONIBLE
        ));

        controlCupos.asignarCupo(1L, TipoVehiculo.CARRO);

        assertThrows(
            ParqueaderoLlenoException.class,
            () -> controlCupos.asignarCupo(
                1L, TipoVehiculo.CARRO
            )
        );
    }

    private static class RepositorioEnMemoria
            implements CupoRepository {

        private final List<Cupo> cupos = new ArrayList<>();

        @Override
        public Optional<Cupo> buscarDisponible(
                Long sedeId, TipoVehiculo tipoVehiculo) {

            return cupos.stream()
                .filter(c -> c.getSedeId().equals(sedeId))
                .filter(c -> c.getTipoVehiculo() == tipoVehiculo)
                .filter(Cupo::estaDisponible)
                .findFirst();
        }

        @Override
        public Optional<Cupo> buscarPorId(Long id) {

            return cupos.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
        }

        @Override
        public long contarDisponibles(
                Long sedeId, TipoVehiculo tipoVehiculo) {

            return cupos.stream()
                .filter(c -> c.getSedeId().equals(sedeId))
                .filter(c -> c.getTipoVehiculo() == tipoVehiculo)
                .filter(Cupo::estaDisponible)
                .count();
        }

        @Override
        public Cupo guardar(Cupo cupo) {

            cupos.removeIf(
                c -> c.getId().equals(cupo.getId())
            );

            cupos.add(cupo);

            return cupo;
        }
    }
}