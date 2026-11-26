package co.edu.uam.parqueadero.dominio.modelo;

import co.edu.uam.parqueadero.dominio.excepciones.PlacaInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlacaTest {

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "ABC 123", "abc-123", "  AbC - 1 2 3 "})
    @DisplayName("Normaliza a mayúsculas y quita espacios y guiones")
    void normalizaLaPlaca(String texto) {
        assertThat(Placa.de(texto).valor()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("Dos placas escritas distinto pero con el mismo valor son iguales")
    void placasNormalizadasSonIguales() {
        assertThat(Placa.de("abc 123")).isEqualTo(Placa.de("ABC-123"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", " - "})
    @DisplayName("Rechaza una placa vacía")
    void rechazaPlacaVacia(String texto) {
        assertThatThrownBy(() -> Placa.de(texto))
                .isInstanceOf(PlacaInvalidaException.class)
                .hasMessage("La placa no puede estar vacía");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB*123", "ÑAB123", "ABC.12"})
    @DisplayName("Rechaza caracteres que no son letras ni números")
    void rechazaCaracteresInvalidos(String texto) {
        assertThatThrownBy(() -> Placa.de(texto))
                .isInstanceOf(PlacaInvalidaException.class)
                .hasMessageContaining("solo puede tener letras y números");
    }
}
