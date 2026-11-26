package co.edu.uam.parqueadero.dominio.excepciones;

import co.edu.uam.parqueadero.dominio.modelo.Placa;

public class VehiculoNoClasificableException extends ExcepcionDeDominio {

    public VehiculoNoClasificableException(Placa placa) {
        super("Formato de placa inválido: " + placa.valor() + " no corresponde a un carro ni a una moto");
    }
}
