package co.edu.uam.parqueadero.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Objects;

/** Tarifa por sede, tipo de vehiculo y franja. */
public record Tarifa(Long id, Long sedeId, TipoVehiculo tipoVehiculo,
                     FranjaTarifaria franja, String nombre,
                     LocalTime horaInicio, LocalTime horaFin, BigDecimal valor,
                     boolean activa) {
    public Tarifa {
        Objects.requireNonNull(sedeId, "La sede es obligatoria");
        Objects.requireNonNull(tipoVehiculo, "El tipo de vehiculo es obligatorio");
        Objects.requireNonNull(franja, "La franja es obligatoria");
        Objects.requireNonNull(horaInicio, "La hora inicial es obligatoria");
        Objects.requireNonNull(horaFin, "La hora final es obligatoria");
        Objects.requireNonNull(valor, "El valor es obligatorio");
        if (sedeId <= 0) throw new IllegalArgumentException("La sede debe ser valida");
        if (nombre == null || nombre.isBlank() || nombre.length() > 100)
            throw new IllegalArgumentException("El nombre debe tener entre 1 y 100 caracteres");
        if (horaInicio.equals(horaFin))
            throw new IllegalArgumentException("La hora inicial debe ser diferente de la final");
        if (valor.signum() < 0)
            throw new IllegalArgumentException("La tarifa no puede ser negativa");
    }
}
