package co.edu.uam.parqueadero.dominio.modelo;

import java.time.LocalDateTime;

/**
 * Registro de un ingreso o una salida de un vehículo en una sede.
 * El id es nulo hasta que el evento se guarda.
 */
public record EventoAcceso(
        Long id,
        Long sedeId,
        Placa placa,
        TipoVehiculo tipoVehiculo,
        TipoEvento tipo,
        double confianza,
        boolean lecturaManual,
        LocalDateTime fechaHora) {

    public static EventoAcceso ingreso(Sede sede, Placa placa, TipoVehiculo tipoVehiculo,
                                       LecturaPlaca lectura, LocalDateTime fechaHora) {
        return new EventoAcceso(null, sede.id(), placa, tipoVehiculo, TipoEvento.INGRESO,
                lectura.confianza(), lectura.manual(), fechaHora);
    }

    public EventoAcceso conId(Long nuevoId) {
        return new EventoAcceso(nuevoId, sedeId, placa, tipoVehiculo, tipo, confianza, lecturaManual, fechaHora);
    }
}
