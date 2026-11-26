package co.edu.uam.parqueadero.dominio.modelo;

import co.edu.uam.parqueadero.dominio.excepciones.PlacaInvalidaException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Objeto de valor: una placa siempre está normalizada (mayúsculas, sin espacios ni guiones).
 * Dos placas son iguales si su valor normalizado es igual, por eso "abc-123" y "ABC 123" son la misma.
 */
public record Placa(String valor) {

    private static final Pattern SEPARADORES = Pattern.compile("[\\s-]");
    private static final Pattern ALFANUMERICO = Pattern.compile("[A-Z0-9]+");

    public Placa {
        valor = valor == null ? "" : SEPARADORES.matcher(valor).replaceAll("").toUpperCase(Locale.ROOT);
        if (valor.isEmpty()) {
            throw new PlacaInvalidaException("La placa no puede estar vacía");
        }
        if (!ALFANUMERICO.matcher(valor).matches()) {
            throw new PlacaInvalidaException("La placa solo puede tener letras y números: " + valor);
        }
    }

    public static Placa de(String texto) {
        return new Placa(texto);
    }

    @Override
    public String toString() {
        return valor;
    }
}
