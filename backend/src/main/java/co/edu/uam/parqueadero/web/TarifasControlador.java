package co.edu.uam.parqueadero.web;

import co.edu.uam.parqueadero.aplicacion.AdministrarTarifasServicio;
import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.Tarifa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Administracion de tarifas: estas rutas son exclusivas de ADMIN al integrar HU-06. */
@RestController
@RequestMapping("/api/admin/sedes/{sedeId}/tarifas")
public class TarifasControlador {
    private final AdministrarTarifasServicio servicio;

    public TarifasControlador(AdministrarTarifasServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Tarifa> listar(@PathVariable Long sedeId) {
        return servicio.listar(sedeId);
    }

    /** PUT crea o actualiza una unica tarifa activa de una franja y un tipo. */
    @PutMapping("/{tipo}/{franja}")
    public Tarifa configurar(@PathVariable Long sedeId,
                            @PathVariable TipoVehiculo tipo,
                            @PathVariable FranjaTarifaria franja,
                            @Valid @RequestBody TarifaSolicitud solicitud) {
        return servicio.configurar(sedeId, tipo, franja, solicitud.nombre(),
                solicitud.horaInicio(), solicitud.horaFin(), solicitud.valor());
    }

    public record TarifaSolicitud(@NotBlank String nombre,
                                 @NotNull LocalTime horaInicio,
                                 @NotNull LocalTime horaFin,
                                 @NotNull @DecimalMin("0.0") BigDecimal valor) { }
}
