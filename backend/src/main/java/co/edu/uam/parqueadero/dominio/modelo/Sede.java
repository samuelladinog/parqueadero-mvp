package co.edu.uam.parqueadero.dominio.modelo;

/**
 * Sede del parqueadero. El umbral de confianza (0 a 100) es configurable por sede, sin cambiar código.
 */
public record Sede(Long id, String nombre, double umbralConfianza) {

    public boolean aceptaAutomaticamente(LecturaPlaca lectura) {
        return lectura.confianza() >= umbralConfianza;
    }
}
