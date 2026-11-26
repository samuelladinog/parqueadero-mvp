package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.clasificacion.ClasificadorVehiculo;
import co.edu.uam.parqueadero.dominio.excepciones.LecturaDudosaException;
import co.edu.uam.parqueadero.dominio.excepciones.PlacaInvalidaException;
import co.edu.uam.parqueadero.dominio.excepciones.SedeNoEncontradaException;
import co.edu.uam.parqueadero.dominio.excepciones.VehiculoNoClasificableException;
import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.Sede;
import co.edu.uam.parqueadero.dominio.modelo.TipoEvento;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioEventos;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioSedes;
import co.edu.uam.parqueadero.infraestructura.lector.AdaptadorCamaraLpr;
import co.edu.uam.parqueadero.infraestructura.lector.LectorManual;
import co.edu.uam.parqueadero.infraestructura.lector.ResultadoCamaraLpr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarIngresoServicioTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-26T15:00:00Z"), BOGOTA);
    private static final Sede SEDE = new Sede(1L, "Sede centro", 85.0);

    private final EventosEnMemoria eventos = new EventosEnMemoria();
    private final RepositorioSedes sedes = id -> id.equals(SEDE.id()) ? Optional.of(SEDE) : Optional.empty();
    private final RegistrarIngresoServicio servicio =
            new RegistrarIngresoServicio(ClasificadorVehiculo.conReglasColombianas(), sedes, eventos, RELOJ);

    @Test
    @DisplayName("Éxito: 'abc 123' con confianza 0.95 registra un INGRESO de ABC123 con fecha, hora y sede")
    void registraIngresoConConfianzaSuficiente() {
        EventoAcceso evento = servicio.registrar(1L, camara("abc 123", 0.95));

        assertThat(evento.id()).isNotNull();
        assertThat(evento.placa()).isEqualTo(Placa.de("ABC123"));
        assertThat(evento.tipo()).isEqualTo(TipoEvento.INGRESO);
        assertThat(evento.tipoVehiculo()).isEqualTo(TipoVehiculo.CARRO);
        assertThat(evento.sedeId()).isEqualTo(1L);
        assertThat(evento.confianza()).isEqualTo(95.0);
        assertThat(evento.lecturaManual()).isFalse();
        assertThat(evento.fechaHora()).isEqualTo(LocalDateTime.of(2026, 11, 26, 10, 0));
        assertThat(eventos.guardados).containsExactly(evento);
    }

    @Test
    @DisplayName("Una confianza exactamente igual al umbral también se acepta")
    void aceptaConfianzaIgualAlUmbral() {
        EventoAcceso evento = servicio.registrar(1L, camara("XYZ98B", 0.85));

        assertThat(evento.tipoVehiculo()).isEqualTo(TipoVehiculo.MOTO);
    }

    @Test
    @DisplayName("Error: 'AB-12' se rechaza con 'formato de placa inválido' y no se registra nada")
    void rechazaFormatoInvalido() {
        assertThatThrownBy(() -> servicio.registrar(1L, camara("AB-12", 0.99)))
                .isInstanceOf(VehiculoNoClasificableException.class)
                .hasMessageStartingWith("Formato de placa inválido");
        assertThat(eventos.guardados).isEmpty();
    }

    @Test
    @DisplayName("Una placa vacía se rechaza y no se registra nada")
    void rechazaPlacaVacia() {
        assertThatThrownBy(() -> servicio.registrar(1L, camara("  ", 0.99)))
                .isInstanceOf(PlacaInvalidaException.class);
        assertThat(eventos.guardados).isEmpty();
    }

    @Test
    @DisplayName("Confianza menor al umbral: no registra y pide validación del operador")
    void noRegistraLecturaDudosa() {
        assertThatThrownBy(() -> servicio.registrar(1L, camara("ABC123", 0.40)))
                .isInstanceOf(LecturaDudosaException.class)
                .hasMessageContaining("menor al umbral");
        assertThat(eventos.guardados).isEmpty();
    }

    @Test
    @DisplayName("El lector manual es intercambiable con la cámara y marca la lectura como manual")
    void aceptaLectorManual() {
        EventoAcceso evento = servicio.registrar(1L, new LectorManual("abc-123"));

        assertThat(evento.placa()).isEqualTo(Placa.de("ABC123"));
        assertThat(evento.lecturaManual()).isTrue();
        assertThat(evento.confianza()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Una sede que no existe se rechaza")
    void rechazaSedeInexistente() {
        assertThatThrownBy(() -> servicio.registrar(99L, camara("ABC123", 0.95)))
                .isInstanceOf(SedeNoEncontradaException.class);
    }

    private static AdaptadorCamaraLpr camara(String placa, double score) {
        return new AdaptadorCamaraLpr(new ResultadoCamaraLpr(placa, score));
    }

    private static final class EventosEnMemoria implements RepositorioEventos {
        private final List<EventoAcceso> guardados = new ArrayList<>();

        @Override
        public EventoAcceso guardar(EventoAcceso evento) {
            EventoAcceso conId = evento.conId((long) guardados.size() + 1);
            guardados.add(conId);
            return conId;
        }
    }
}
