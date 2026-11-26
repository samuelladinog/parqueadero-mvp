package co.edu.uam.parqueadero.dominio.clasificacion;

import co.edu.uam.parqueadero.dominio.excepciones.VehiculoNoClasificableException;
import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

import java.util.List;

/**
 * Determina si un vehículo es carro o moto a partir de su placa (F-02).
 * Recorre las reglas recibidas y usa la primera que reconozca la placa; si ninguna aplica, la rechaza.
 */
public class ClasificadorVehiculo {

    private final List<ReglaClasificacion> reglas;

    public ClasificadorVehiculo(List<ReglaClasificacion> reglas) {
        if (reglas == null || reglas.isEmpty()) {
            throw new IllegalArgumentException("El clasificador necesita al menos una regla");
        }
        this.reglas = List.copyOf(reglas);
    }

    /** Clasificador con las reglas colombianas de carro y moto. */
    public static ClasificadorVehiculo conReglasColombianas() {
        return new ClasificadorVehiculo(List.of(ReglaPorFormato.CARRO, ReglaPorFormato.MOTO));
    }

    public TipoVehiculo clasificar(Placa placa) {
        return reglas.stream()
                .filter(regla -> regla.aplicaA(placa))
                .map(ReglaClasificacion::tipo)
                .findFirst()
                .orElseThrow(() -> new VehiculoNoClasificableException(placa));
    }
}
