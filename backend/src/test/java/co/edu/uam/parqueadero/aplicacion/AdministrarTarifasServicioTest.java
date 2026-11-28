package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.Tarifa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioTarifas;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdministrarTarifasServicioTest {
    private RepositorioEnMemoria repo;
    private AdministrarTarifasServicio servicio;

    @BeforeEach
    void preparar() {
        repo = new RepositorioEnMemoria();
        servicio = new AdministrarTarifasServicio(repo);
    }

    private Tarifa guardar(TipoVehiculo tipo, FranjaTarifaria franja, String precio) {
        LocalTime inicio = franja == FranjaTarifaria.DIURNA ? LocalTime.of(6, 0) : LocalTime.of(18, 0);
        LocalTime fin = franja == FranjaTarifaria.DIURNA ? LocalTime.of(18, 0) : LocalTime.of(6, 0);
        return servicio.configurar(1L, tipo, franja, "Tarifa de prueba", inicio, fin, new BigDecimal(precio));
    }

    @Test
    void permiteCarroDiurno10000YNocturno8000() {
        guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "10000");
        Tarifa nocturna = guardar(TipoVehiculo.CARRO, FranjaTarifaria.NOCTURNA, "8000");
        assertEquals(0, nocturna.valor().compareTo(new BigDecimal("8000")));
        assertEquals(2, servicio.listar(1L).size());
    }

    @Test
    void rechazaNocturnaMayorOIgualALaDiurna() {
        guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "10000");
        assertThrows(IllegalArgumentException.class,
                () -> guardar(TipoVehiculo.CARRO, FranjaTarifaria.NOCTURNA, "12000"));
        assertThrows(IllegalArgumentException.class,
                () -> guardar(TipoVehiculo.CARRO, FranjaTarifaria.NOCTURNA, "10000"));
    }

    @Test
    void rechazaBajarDiurnaPorDebajoDeNocturna() {
        guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "10000");
        guardar(TipoVehiculo.CARRO, FranjaTarifaria.NOCTURNA, "8000");
        assertThrows(IllegalArgumentException.class,
                () -> guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "7000"));
    }

    @Test
    void motoPuedeSerGratisEnAmbasFranjas() {
        guardar(TipoVehiculo.MOTO, FranjaTarifaria.DIURNA, "0");
        guardar(TipoVehiculo.MOTO, FranjaTarifaria.NOCTURNA, "0");
        assertEquals(2, servicio.listar(1L).size());
    }

    @Test
    void rechazaValoresNegativosYOtraHoraIgual() {
        assertThrows(IllegalArgumentException.class,
                () -> guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "-1"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.configurar(1L, TipoVehiculo.CARRO, FranjaTarifaria.DIURNA,
                        "Carro", LocalTime.NOON, LocalTime.NOON, BigDecimal.TEN));
    }

    @Test
    void actualizarMismaCombinacionNoCreaDuplicados() {
        guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "10000");
        guardar(TipoVehiculo.CARRO, FranjaTarifaria.DIURNA, "11000");
        assertEquals(1, servicio.listar(1L).size());
        assertEquals(0, servicio.listar(1L).getFirst().valor().compareTo(new BigDecimal("11000")));
    }

    static class RepositorioEnMemoria implements RepositorioTarifas {
        private final List<Tarifa> registros = new ArrayList<>();
        private long siguienteId = 1;
        @Override
        public Optional<Tarifa> buscarActiva(Long sedeId, TipoVehiculo tipo, FranjaTarifaria franja) {
            return registros.stream().filter(t -> t.sedeId().equals(sedeId)
                    && t.tipoVehiculo() == tipo && t.franja() == franja && t.activa()).findFirst();
        }
        @Override
        public List<Tarifa> listarPorSede(Long sedeId) {
            return registros.stream().filter(t -> t.sedeId().equals(sedeId)).toList();
        }
        @Override
        public Tarifa guardar(Tarifa tarifa) {
            Long id = tarifa.id() == null ? siguienteId++ : tarifa.id();
            registros.removeIf(t -> t.id().equals(id));
            Tarifa resultado = new Tarifa(id, tarifa.sedeId(), tarifa.tipoVehiculo(),
                    tarifa.franja(), tarifa.nombre(), tarifa.horaInicio(),
                    tarifa.horaFin(), tarifa.valor(), tarifa.activa());
            registros.add(resultado);
            return resultado;
        }
    }
}
