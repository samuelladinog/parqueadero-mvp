package co.edu.uam.parqueadero.infraestructura.lector;

/**
 * Formato propio de la cámara de reconocimiento de placas (LPR) que usamos como simulador.
 * Es una interfaz "externa": no la diseñamos nosotros, por eso no coincide con {@code LecturaPlaca}.
 *
 * @param plate texto reconocido por la cámara, tal como lo entrega
 * @param score certeza del reconocimiento, de 0.0 a 1.0
 */
public record ResultadoCamaraLpr(String plate, double score) {
}
