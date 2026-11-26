package co.edu.uam.parqueadero.dominio.clasificacion;

import co.edu.uam.parqueadero.dominio.excepciones.VehiculoNoClasificableException;
import co.edu.uam.parqueadero.dominio.modelo.Placa;
import co.edu.uam.parqueadero.dominio.modelo.TipoVehiculo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClasificadorVehiculoTest {

    private final ClasificadorVehiculo clasificador = ClasificadorVehiculo.conReglasColombianas();

    @ParameterizedTest
    @ValueSource(strings = {"AAA123", "abc 987", "XYZ-001"})
    @DisplayName("Tres letras y tres números es CARRO")
    void clasificaCarro(String placa) {
        assertThat(clasificador.clasificar(Placa.de(placa))).isEqualTo(TipoVehiculo.CARRO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"AAA12A", "XYZ98B", "abc 12d"})
    @DisplayName("Tres letras, dos números y una letra es MOTO")
    void clasificaMoto(String placa) {
        assertThat(clasificador.clasificar(Placa.de(placa))).isEqualTo(TipoVehiculo.MOTO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234AB", "AB123", "ABCD123", "ABC1234", "ABC12", "123ABC"})
    @DisplayName("Cualquier otro formato se rechaza")
    void rechazaFormatoDesconocido(String placa) {
        assertThatThrownBy(() -> clasificador.clasificar(Placa.de(placa)))
                .isInstanceOf(VehiculoNoClasificableException.class)
                .hasMessageContaining("no corresponde a un carro ni a una moto");
    }

    @Test
    @DisplayName("Abierto/cerrado: una regla nueva se agrega sin modificar el clasificador")
    void aceptaReglasNuevasSinModificarse() {
        ReglaClasificacion motoFormatoAntiguo = new ReglaPorFormato("[A-Z]{3}[0-9]{2}", TipoVehiculo.MOTO);
        ClasificadorVehiculo extendido = new ClasificadorVehiculo(
                List.of(ReglaPorFormato.CARRO, ReglaPorFormato.MOTO, motoFormatoAntiguo));

        assertThat(extendido.clasificar(Placa.de("ABC12"))).isEqualTo(TipoVehiculo.MOTO);
        assertThat(extendido.clasificar(Placa.de("ABC123"))).isEqualTo(TipoVehiculo.CARRO);
    }

    @Test
    @DisplayName("No se puede crear un clasificador sin reglas")
    void exigeAlMenosUnaRegla() {
        assertThatThrownBy(() -> new ClasificadorVehiculo(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
