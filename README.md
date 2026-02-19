# MusicToMe 🎵

Aplicación de reproducción de música para Android simple y eficiente.

## 🚀 Características
- **Reproducción Profesional**: Basada en Jetpack Media3 para una gestión óptima de audio.
- **Segundo Plano**: Soporta controles desde la notificación del sistema y dispositivos Bluetooth.
- **Memoria Inteligente**: Gracias a DataStore, la app recuerda tu última canción incluso después de reiniciarse.
- **UI Reactiva**: Interfaz fluida construida al 100% con Jetpack Compose.

## 🛠️ Tecnologías utilizadas
- [Kotlin](https://kotlinlang.org/) - Lenguaje principal.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Toolkit moderno para UI.
- [Media3 / ExoPlayer](https://developer.android.com/guide/topics/media/media3) - Motor de reproducción.
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Inyección de dependencias.
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Almacenamiento de preferencias.

## 📱 Requisitos
- Android 8.0 (API 26) o superior.
- Permisos de lectura de almacenamiento concedidos.

## 🛠️ Instalación
1. Clona el repositorio:
   ```bash
   git clone [https://github.com/tu-usuario/musictome.git](https://github.com/tu-usuario/musictome.git)

2. Abre el proyecto en Android Studio Jellyfish o superior.

3. Sincroniza Gradle y ejecuta la app en un dispositivo real o emulador.

4. 🧪 Pruebas y Compatibilidad

La aplicación ha sido sometida a pruebas de estrés y rendimiento en entornos reales, asegurando estabilidad en sistemas operativos con configuraciones de seguridad avanzada.
Entorno de Pruebas Principal

    Dispositivo: Google Pixel 10 PRO (Marca Móvil de referencia para GrapheneOS).

    Sistema Operativo: GrapheneOS (Basado en Android 16 Compilacion 2026021201).

    Configuración: Sin Google Play Services (Sandboxed Play Services opcionales).

Puntos Verificados en GrapheneOS

    Gestión de Permisos: Funcionamiento correcto del acceso a archivos locales mediante el Storage Access Framework.

    Ciclo de Vida del Servicio: El PlaybackService se mantiene estable bajo las estrictas políticas de ahorro de energía de GrapheneOS.

    Privacidad: La aplicación no requiere conexión a red ni telemetría, alineándose con los principios del sistema operativo.

    Notificaciones de Media: Los controles en la pantalla de bloqueo y el panel de ajustes rápidos responden sin retardos (latencia mínima).

5. 🛠️ Estado del Proyecto y Próximos Pasos
Puedes ver las tareas pendientes y los errores identificados en nuestra sección de [Issues](https://github.com/DaniFbd/MusicToMe/issues).


## ⚖️ Licencia
Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

## 👨‍💻 Autor

**Daniel Cano Nicolau** *Desarrollador Java especializado en aplicaciones Web | Explorando el ecosistema Android con Kotlin y Jetpack Compose.*

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/dani-cano-nicolau-a33256277/)

Si encuentras un error que no esté en esta lista, por favor abre un Issue en este repositorio.
