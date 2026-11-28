package co.edu.uam.parqueadero.web;

import co.edu.uam.parqueadero.aplicacion.RegistrarIngresoServicio;
import co.edu.uam.parqueadero.dominio.excepciones.LecturaDudosaException;
import co.edu.uam.parqueadero.dominio.excepciones.SedeNoEncontradaException;
import co.edu.uam.parqueadero.dominio.excepciones.VehiculoNoClasificableException;
import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.TipoEvento;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.LectorPlacas;
import co.edu.uam.parqueadero.infraestructura.lector.AdaptadorCamaraLpr;
import co.edu.uam.parqueadero.infraestructura.lector.LectorManual;
import co.edu.uam.parqueadero.infraestructura.lector.LectorValidadoPorOperador;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngresoControlador.class)
@WithMockUser
class IngresoControladorTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RegistrarIngresoServicio registrarIngreso;

    private static final EventoAcceso EVENTO = new EventoAcceso(7L, 1L, Placa.de("ABC123"), TipoVehiculo.CARRO,
            TipoEvento.INGRESO, 95.0, false, null, LocalDateTime.of(2026, 11, 26, 10, 0));

    @Test
    void registraDesdeLaCamaraUsandoElAdaptador() throws Exception {
        when(registrarIngreso.registrar(eq(1L), any())).thenReturn(EVENTO);

        mvc.perform(post("/api/sedes/1/ingresos/camara").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\": \"abc 123\", \"score\": 0.95}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.placa").value("ABC123"))
                .andExpect(jsonPath("$.tipo").value("INGRESO"))
                .andExpect(jsonPath("$.tipoVehiculo").value("CARRO"));

        ArgumentCaptor<LectorPlacas> lector = ArgumentCaptor.forClass(LectorPlacas.class);
        verify(registrarIngreso).registrar(eq(1L), lector.capture());
        assertThat(lector.getValue()).isInstanceOf(AdaptadorCamaraLpr.class);
        assertThat(lector.getValue().leer().confianza()).isEqualTo(95.0);
    }

    @Test
    void registraManualUsandoElLectorManual() throws Exception {
        when(registrarIngreso.registrar(eq(1L), any())).thenReturn(EVENTO);

        mvc.perform(post("/api/sedes/1/ingresos/manual").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\": \"abc-123\"}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<LectorPlacas> lector = ArgumentCaptor.forClass(LectorPlacas.class);
        verify(registrarIngreso).registrar(eq(1L), lector.capture());
        assertThat(lector.getValue()).isInstanceOf(LectorManual.class);
    }

    @Test
    void formatoInvalidoResponde422ConMensajeClaro() throws Exception {
        when(registrarIngreso.registrar(eq(1L), any()))
                .thenThrow(new VehiculoNoClasificableException(Placa.de("AB12")));

        mvc.perform(post("/api/sedes/1/ingresos/camara").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\": \"AB-12\", \"score\": 0.99}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value(
                        "Formato de placa inválido: AB12 no corresponde a un carro ni a una moto"));
    }

    @Test
    void lecturaDudosaResponde409YPideValidacion() throws Exception {
        when(registrarIngreso.registrar(eq(1L), any()))
                .thenThrow(new LecturaDudosaException(Placa.de("ABC123"), 40.0, 85.0));

        mvc.perform(post("/api/sedes/1/ingresos/camara").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\": \"ABC123\", \"score\": 0.40}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.requiereValidacion").value(true))
                .andExpect(jsonPath("$.placaLeida").value("ABC123"));
    }

    @Test
    void sedeInexistenteResponde404() throws Exception {
        when(registrarIngreso.registrar(eq(99L), any())).thenThrow(new SedeNoEncontradaException(99L));

        mvc.perform(post("/api/sedes/99/ingresos/manual").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\": \"ABC123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void solicitudSinPlacaResponde400SinLlamarAlCasoDeUso() throws Exception {
        mvc.perform(post("/api/sedes/1/ingresos/manual").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\": \"\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registrarIngreso);
    }

    @Test
    void validacionDelOperadorUsaElLectorValidadoYConservaLaConfianza() throws Exception {
        when(registrarIngreso.registrar(eq(1L), any())).thenReturn(EVENTO);

        mvc.perform(post("/api/sedes/1/ingresos/validacion").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placaLeida\": \"ABC128\", \"placaConfirmada\": \"ABC123\", \"confianza\": 40.0}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<LectorPlacas> lector = ArgumentCaptor.forClass(LectorPlacas.class);
        verify(registrarIngreso).registrar(eq(1L), lector.capture());
        assertThat(lector.getValue()).isInstanceOf(LectorValidadoPorOperador.class);
        assertThat(lector.getValue().leer().manual()).isTrue();
        assertThat(lector.getValue().leer().confianza()).isEqualTo(40.0);
    }

    @Test
    void validacionConPlacaVaciaResponde400() throws Exception {
        mvc.perform(post("/api/sedes/1/ingresos/validacion").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placaLeida\": \"ABC128\", \"placaConfirmada\": \" \", \"confianza\": 40.0}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registrarIngreso);
    }
}
