package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.clasificacion.ClasificadorVehiculo;
import co.edu.uam.parqueadero.dominio.excepciones.LecturaDudosaException;
import co.edu.uam.parqueadero.dominio.excepciones.SedeNoEncontradaException;
import co.edu.uam.parqueadero.dominio.modelo.EventoAcceso;
import co.edu.uam.parqueadero.dominio.modelo.LecturaPlaca;
import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.Sede;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.LectorPlacas;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioEventos;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioSedes;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Caso de uso F-01: registrar el ingreso de un vehículo a partir de la placa leída.
 * <ol>
 *     <li>Lee la placa con el lector recibido (cámara, simulador o digitación).</li>
 *     <li>Normaliza la placa y la clasifica; un formato inválido se rechaza.</li>
 *     <li>Si la confianza no alcanza el umbral de la sede, no registra nada y pide validación (HU-03).</li>
 *     <li>Registra el evento INGRESO con la fecha y hora del reloj inyectado.</li>
 * </ol>
 */
public class RegistrarIngresoServicio {

    private final ClasificadorVehiculo clasificador;
    private final RepositorioSedes sedes;
    private final RepositorioEventos eventos;
    private final Clock reloj;

    public RegistrarIngresoServicio(ClasificadorVehiculo clasificador, RepositorioSedes sedes,
                                    RepositorioEventos eventos, Clock reloj) {
        this.clasificador = clasificador;
        this.sedes = sedes;
        this.eventos = eventos;
        this.reloj = reloj;
    }

    public EventoAcceso registrar(Long sedeId, LectorPlacas lector) {
        Sede sede = sedes.buscarPorId(sedeId).orElseThrow(() -> new SedeNoEncontradaException(sedeId));
        LecturaPlaca lectura = lector.leer();
        Placa placa = Placa.de(lectura.textoLeido());
        TipoVehiculo tipo = clasificador.clasificar(placa);

        if (!sede.aceptaAutomaticamente(lectura)) {
            throw new LecturaDudosaException(placa, lectura.confianza(), sede.umbralConfianza());
        }

        EventoAcceso ingreso = EventoAcceso.ingreso(sede, placa, tipo, lectura, LocalDateTime.now(reloj));
        return eventos.guardar(ingreso);
    }
}
