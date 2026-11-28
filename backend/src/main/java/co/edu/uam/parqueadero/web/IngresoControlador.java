package co.edu.uam.parqueadero.web;

import co.edu.uam.parqueadero.aplicacion.RegistrarIngresoServicio;
import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.infraestructura.lector.AdaptadorCamaraLpr;
import co.edu.uam.parqueadero.infraestructura.lector.LectorManual;
import co.edu.uam.parqueadero.infraestructura.lector.ResultadoCamaraLpr;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Ingreso de vehículos (F-01). Dos entradas que usan el mismo caso de uso con lectores distintos:
 * la cámara (o su simulador) y la digitación del operador.
 */
@RestController
@RequestMapping("/api/sedes/{sedeId}/ingresos")
public class IngresoControlador {

    private final RegistrarIngresoServicio registrarIngreso;

    public IngresoControlador(RegistrarIngresoServicio registrarIngreso) {
        this.registrarIngreso = registrarIngreso;
    }

    /** Lectura que envía la cámara LPR en su propio formato: {"plate": "ABC 123", "score": 0.95}. */
    @PostMapping("/camara")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoRespuesta registrarDesdeCamara(@PathVariable Long sedeId,
                                                @Valid @RequestBody LecturaCamaraSolicitud solicitud) {
        var lector = new AdaptadorCamaraLpr(new ResultadoCamaraLpr(solicitud.plate(), solicitud.score()));
        return EventoRespuesta.desde(registrarIngreso.registrar(sedeId, lector));
    }

    /** Placa digitada por el operador: {"placa": "ABC123"}. */
    @PostMapping("/manual")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoRespuesta registrarManual(@PathVariable Long sedeId,
                                           @Valid @RequestBody LecturaManualSolicitud solicitud) {
        return EventoRespuesta.desde(registrarIngreso.registrar(sedeId, new LectorManual(solicitud.placa())));
    }

    public record LecturaCamaraSolicitud(@NotBlank String plate, @NotNull Double score) {
    }

    public record LecturaManualSolicitud(@NotBlank String placa) {
    }

    public record EventoRespuesta(Long id, Long sedeId, String placa, String tipoVehiculo, String tipo,
                                  double confianza, boolean lecturaManual, LocalDateTime fechaHora) {

        static EventoRespuesta desde(EventoAcceso evento) {
            return new EventoRespuesta(evento.id(), evento.sedeId(), evento.placa().valor(),
                    evento.tipoVehiculo().name(), evento.tipo().name(), evento.confianza(),
                    evento.lecturaManual(), evento.fechaHora());
        }
    }
}
