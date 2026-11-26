package co.edu.uam.parqueadero.dominio.modelo;

/**
 * Sede del parqueadero. El umbral de confianza (0 a 100) es configurable por sede, sin cambiar código.
 */
public record Sede(Long id, String nombre, double umbralConfianza) {

    /**
     * Una lectura se registra sin intervención si alcanza el umbral. Las lecturas manuales ya fueron
     * validadas por el operador, así que no dependen del umbral (HU-03).
     */
    public boolean aceptaAutomaticamente(LecturaPlaca lectura) {
        return lectura.manual() || lectura.confianza() >= umbralConfianza;
    }
}
