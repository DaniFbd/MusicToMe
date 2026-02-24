MusicToMe 🎵

Aplicación de reproducción de música para Android, simple, eficiente y diseñada bajo estándares modernos de desarrollo.
🚀 Características

    Gestión de Listas: Crea, edita y elimina tus propias playlists locales con un sistema de menús contextuales (Long Press).

    Reproducción Profesional: Basada en Jetpack Media3 para una gestión de audio fluida y robusta.

    Segundo Plano Inteligente: PlaybackService optimizado para no consumir batería innecesariamente, con controles desde la notificación y Bluetooth.

    Memoria de Sesión: La app recuerda tu última canción, el modo aleatorio y la repetición gracias a DataStore.

    Arquitectura Reactiva: Interfaz 100% construida con Jetpack Compose, sincronizada en tiempo real con la base de datos.

    Buscador Integrado: Encuentra cualquier canción en tu biblioteca al instante.

🛠️ Tecnologías utilizadas

    Kotlin: Lenguaje moderno y seguro.

    Jetpack Compose: UI declarativa de alto rendimiento.

    Media3 / ExoPlayer: El motor de Google para reproducción multimedia.

    Room Database: Persistencia local para tus playlists y metadatos.

    Hilt (Dagger): Inyección de dependencias para un código limpio y testeable.

    DataStore: Alternativa moderna y reactiva a SharedPreferences.

📱 Requisitos y Compatibilidad

    Android 8.0 (API 26) o superior.

    GrapheneOS Ready: Probada en entornos de máxima privacidad sin dependencia de Google Play Services.

🧪 Pruebas de Rendimiento (Referencia 2026)

La aplicación ha sido validada en configuraciones de seguridad avanzada:

    Entorno: Google Pixel 10 PRO sobre GrapheneOS (Android 16).

    Puntos Verificados:

        Ciclo de vida: Cierre total de procesos al eliminar la app de "Recientes".

        Latencia: Respuesta instantánea en controles de medios y cambios de metadatos.

        Privacidad: Cero telemetría y funcionamiento 100% offline.

🛠️ Instalación y Uso

    Clona el repositorio:
    Bash

    git clone https://github.com/DaniFbd/MusicToMe.git

    Abre el proyecto en Android Studio Jellyfish (o superior).

    Compila: Asegúrate de conceder permisos de lectura de medios al arrancar.

👨‍💻 Autor

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/dani-cano-nicolau-a33256277/)


Si encuentras un error que no esté en esta lista, por favor abre un Issue en este repositorio. 
