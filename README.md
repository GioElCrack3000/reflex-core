Reflex Core - Project skeleton (ready for Android Studio)
========================================================

¿Qué contiene este ZIP?
- Proyecto mínimo de Android (módulo app).
- Código en Kotlin, 100% generado (sin assets).
- Implementación SurfaceView con loop, player, obstacles, score, high score y feedback visual.

Importante:
- No puedo compilar un APK en este entorno (no hay Android SDK/Gradle completo aquí).
- Lo que sí hice: empaqueté todo el código necesario para **abrir el proyecto en Android Studio** y generar el APK allí.
- Sigue las instrucciones abajo para obtener un APK instalable.

Cómo generar el APK (pasos):
1. Descarga y descomprime este archivo.
2. Abre Android Studio (recomiendo Chipmunk o Dolphin o versión reciente).
3. File > Open > selecciona la carpeta 'NeonMind' que descomprimiste.
4. Android Studio puede preguntarte por Gradle/Plugin versions; acepta que descargue dependencias.
5. Conecta tu teléfono o usa un emulador.
6. Run > Run 'app' para instalar el APK in tu dispositivo.
   - Para generar un APK firmado: Build > Generate Signed Bundle / APK...

Notas técnicas:
- Min SDK: 23
- Compile SDK: 34
- No internet required para el juego en runtime.
- Anuncios no incluidos; he dejado puntos en el código donde integrarlos (deathCount).

Si quieres, puedo:
- Añadir un Gradle wrapper al ZIP (para builds reproducibles).
- Generar un AAB-compatible manifest and proguard files for release.
- Incluir instrucciones para firmar the APK.
- Integrar AdMob stub code (sin claves) para pruebas.
