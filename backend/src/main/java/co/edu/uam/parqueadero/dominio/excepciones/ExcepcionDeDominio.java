package co.edu.uam.parqueadero.dominio.excepciones;

/**
 * Base de las violaciones a reglas de negocio. La capa web la traduce a una respuesta con mensaje claro.
 */
public abstract class ExcepcionDeDominio extends RuntimeException {

    protected ExcepcionDeDominio(String mensaje) {
        super(mensaje);
    }
}
