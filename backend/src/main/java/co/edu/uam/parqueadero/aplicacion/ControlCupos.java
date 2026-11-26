package co.edu.uam.parqueadero.aplicacion;

import co.edu.uam.parqueadero.dominio.modelo.Cupo;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import co.edu.uam.parqueadero.dominio.puertos.CupoRepository;
import co.edu.uam.parqueadero.dominio.excepciones.ParqueaderoLlenoException;

import java.util.Objects;

public class ControlCupos {

    private final CupoRepository cupoRepository;

    public ControlCupos(CupoRepository cupoRepository) {
        this.cupoRepository = Objects.requireNonNull(cupoRepository);
    }

    public long consultarDisponibilidad(
            Long sedeId, TipoVehiculo tipoVehiculo) {

        return cupoRepository.contarDisponibles(
            sedeId, tipoVehiculo
        );
    }

    public boolean hayDisponibilidad(
            Long sedeId, TipoVehiculo tipoVehiculo) {

        return consultarDisponibilidad(sedeId, tipoVehiculo) > 0;
    }

    public Cupo asignarCupo(
            Long sedeId, TipoVehiculo tipoVehiculo) {

        Cupo cupo = cupoRepository
            .buscarDisponible(sedeId, tipoVehiculo)
            .orElseThrow(() ->
                new ParqueaderoLlenoException(tipoVehiculo)
            );

        cupo.ocupar();

        return cupoRepository.guardar(cupo);
    }

    public Cupo liberarCupo(Long cupoId) {

        Cupo cupo = cupoRepository.buscarPorId(cupoId)
            .orElseThrow(() ->
                new IllegalArgumentException("Cupo no encontrado")
            );

        cupo.liberar();

        return cupoRepository.guardar(cupo);
    }
}