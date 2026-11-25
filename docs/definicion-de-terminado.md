# Definición de terminado (DoD)

La definición de terminado aplica a todas las historias de usuario y tareas de desarrollo del MVP del parqueadero. Una tarea solo se considera **Hecha** cuando se verifican todos los criterios siguientes.

1. **Historia identificada.** El cambio está relacionado con una HU o tarea del tablero y sus criterios de aceptación se han revisado.
2. **Código revisado.** Se creó una rama `feature/HU-XX-descripcion` desde `develop` y un pull request dirigido a `develop`, aprobado por al menos un integrante distinto del autor.
3. **Pruebas correctas.** Las pruebas correspondientes al cambio se ejecutan correctamente y la verificación automática de GitHub Actions (CI) termina satisfactoriamente.
4. **Sin secretos.** No se incluyen contraseñas reales, claves privadas, tokens ni archivos `.env` en el repositorio.
5. **Configuración documentada.** Toda variable de entorno nueva figura en `.env.example`, sin valores secretos, y se explica cuando sea necesario.
6. **Integración completada.** Los cambios aprobados se fusionaron a `develop` mediante el pull request, sin commits directos a `develop` ni a `main`.
7. **Tablero actualizado.** La tarjeta correspondiente se mueve a `Hecho` y conserva la evidencia útil (PR, pruebas o captura).

## Criterios adicionales de calidad

- El código sigue la arquitectura acordada y mantiene responsabilidades claras.
- Los criterios de aceptación de la historia se comprobaron, incluyendo casos exitosos y de error cuando aplique.
- Si se modifican endpoints, reglas de negocio o configuración, se actualiza la documentación afectada.

## Evidencias esperadas

- Enlace al PR con aprobación de otro integrante.
- Resultado verde del CI en GitHub Actions.
- Historial del merge a `develop`.
- Historia o tarea marcada `Hecho` en GitHub Projects.

**Importante:** un PR aprobado todavía no está *terminado* si falta integrarlo a `develop` o actualizar el tablero.
