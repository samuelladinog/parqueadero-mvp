package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.FranjaTarifaria;
import co.edu.uam.parqueadero.dominio.modelo.Tarifa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.RepositorioTarifas;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Configura tarifas sin depender de JPA. Se reutiliza TipoVehiculo de HU-02. */
@Service
public class AdministrarTarifasServicio {

    private final RepositorioTarifas repositorio;

    public AdministrarTarifasServicio(RepositorioTarifas repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Tarifa> listar(Long sedeId) {
        validarSede(sedeId);
        return repositorio.listarPorSede(sedeId);
    }

    @Transactional
    public Tarifa configurar(Long sedeId, TipoVehiculo tipo, FranjaTarifaria franja,
                            String nombre, LocalTime horaInicio, LocalTime horaFin,
                            BigDecimal valor) {
        validarSede(sedeId);
        Tarifa actual = new Tarifa(null, sedeId, tipo, franja,
                nombre, horaInicio, horaFin, valor, true);

        // RN-02: precio nocturno estrictamente menor al diurno.
        // Excepcion acordable por el PO: moto gratuita en ambas franjas (RN-01).
        FranjaTarifaria otraFranja = franja == FranjaTarifaria.DIURNA
                ? FranjaTarifaria.NOCTURNA : FranjaTarifaria.DIURNA;
        var otra = repositorio.buscarActiva(sedeId, tipo, otraFranja);
        if (otra.isPresent()) {
            BigDecimal dia = franja == FranjaTarifaria.DIURNA ? valor : otra.get().valor();
            BigDecimal noche = franja == FranjaTarifaria.NOCTURNA ? valor : otra.get().valor();
            boolean motoGratis = tipo == TipoVehiculo.MOTO
                    && dia.signum() == 0 && noche.signum() == 0;
            if (!motoGratis && noche.compareTo(dia) >= 0) {
                throw new IllegalArgumentException("La tarifa nocturna debe ser menor que la diurna");
            }
        }

        // PUT idempotente: si la combinacion existe, conserva su identificador.
        Long id = repositorio.buscarActiva(sedeId, tipo, franja)
                .map(Tarifa::id).orElse(null);
        return repositorio.guardar(new Tarifa(id, actual.sedeId(), tipo, franja,
                actual.nombre(), actual.horaInicio(), actual.horaFin(), actual.valor(), true));
    }

    private static void validarSede(Long sedeId) {
        if (sedeId == null || sedeId <= 0)
            throw new IllegalArgumentException("La sede debe ser valida");
    }
}
