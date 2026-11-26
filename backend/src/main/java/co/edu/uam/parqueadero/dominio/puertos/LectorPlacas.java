package co.edu.uam.parqueadero.dominio.puertos;

import co.edu.uam.parqueadero.dominio.modelo.LecturaPlaca;

/**
 * Puerto de entrada de placas. El caso de uso no sabe si la placa viene de una cámara, de un simulador
 * o de la digitación del operador: cualquier implementación es intercambiable.
 */
public interface LectorPlacas {

    LecturaPlaca leer();
}
