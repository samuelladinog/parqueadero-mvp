package co.edu.uam.parqueadero.dominio.clasificacion;

import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;

import java.util.regex.Pattern;

/**
 * Regla que clasifica cuando la placa cumple una expresión regular.
 */
public class ReglaPorFormato implements ReglaClasificacion {

    /** Placa de carro en Colombia: tres letras y tres números, p. ej. ABC123. */
    public static final ReglaPorFormato CARRO = new ReglaPorFormato("[A-Z]{3}[0-9]{3}", TipoVehiculo.CARRO);

    /** Placa de moto en Colombia: tres letras, dos números y una letra, p. ej. ABC12D. */
    public static final ReglaPorFormato MOTO = new ReglaPorFormato("[A-Z]{3}[0-9]{2}[A-Z]", TipoVehiculo.MOTO);

    private final Pattern formato;
    private final TipoVehiculo tipo;

    public ReglaPorFormato(String expresionRegular, TipoVehiculo tipo) {
        this.formato = Pattern.compile(expresionRegular);
        this.tipo = tipo;
    }

    @Override
    public boolean aplicaA(Placa placa) {
        return formato.matcher(placa.valor()).matches();
    }

    @Override
    public TipoVehiculo tipo() {
        return tipo;
    }
}
