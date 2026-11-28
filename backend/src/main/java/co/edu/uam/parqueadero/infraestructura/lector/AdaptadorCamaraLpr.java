package co.edu.uam.parqueadero.infraestructura.lector;

import co.edu.uam.parqueadero.dominio.modelo.LecturaPlaca;
import co.edu.uam.parqueadero.dominio.puertos.LectorPlacas;

/**
 * Patrón Adapter: convierte el resultado de la cámara LPR (score de 0 a 1) en la {@link LecturaPlaca}
 * que entiende el dominio (confianza de 0 a 100). Si se cambia de cámara, solo se escribe otro adaptador.
 */
public class AdaptadorCamaraLpr implements LectorPlacas {

    private final ResultadoCamaraLpr resultado;

    public AdaptadorCamaraLpr(ResultadoCamaraLpr resultado) {
        this.resultado = resultado;
    }

    @Override
    public LecturaPlaca leer() {
        double confianza = Math.round(resultado.score() * LecturaPlaca.CONFIANZA_MAXIMA * 100) / 100.0;
        return LecturaPlaca.automatica(resultado.plate(), confianza);
    }
}
