package co.edu.uam.parqueadero.infraestructura.lector;

import co.edu.uam.parqueadero.dominio.excepciones.LecturaInvalidaException;
import co.edu.uam.parqueadero.dominio.modelo.LecturaPlaca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdaptadorCamaraLprTest {

    @Test
    @DisplayName("Traduce el score de 0 a 1 de la cámara a confianza de 0 a 100")
    void traduceElFormatoDeLaCamara() {
        LecturaPlaca lectura = new AdaptadorCamaraLpr(new ResultadoCamaraLpr("abc 123", 0.957)).leer();

        assertThat(lectura.textoLeido()).isEqualTo("abc 123");
        assertThat(lectura.confianza()).isEqualTo(95.7);
        assertThat(lectura.manual()).isFalse();
    }

    @Test
    @DisplayName("Un score fuera de rango se rechaza")
    void rechazaScoreFueraDeRango() {
        AdaptadorCamaraLpr adaptador = new AdaptadorCamaraLpr(new ResultadoCamaraLpr("ABC123", 1.5));

        assertThatThrownBy(adaptador::leer).isInstanceOf(LecturaInvalidaException.class);
    }

    @Test
    @DisplayName("El lector manual entrega confianza total y marca la lectura como manual")
    void lectorManual() {
        LecturaPlaca lectura = new LectorManual("XYZ98B").leer();

        assertThat(lectura.confianza()).isEqualTo(100.0);
        assertThat(lectura.manual()).isTrue();
    }
}
