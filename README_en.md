MusicToMe 🎵

A simple and efficient music playback application for Android.
🚀 Features

    Professional Playback: Built on Jetpack Media3 for optimal audio management.

    Background Support: Full support for system notification controls and Bluetooth devices.

    Smart Memory: Thanks to DataStore, the app remembers your last played song and settings even after a reboot.

    Reactive UI: Fluid interface built 100% with Jetpack Compose.

🛠️ Tech Stack

    Kotlin - Core language.

    Jetpack Compose - Modern UI toolkit.

    Media3 / ExoPlayer - Playback engine.

    Hilt - Dependency Injection.

    DataStore - Preference storage.

📱 Requirements

    Android 8.0 (API 26) or higher.

    Storage read permissions granted.

🛠️ Installation

    Clone the repository:
    Bash

    git clone https://github.com/your-username/musictome.git

    Open the project in Android Studio Jellyfish or higher.

    Sync Gradle and run the app on a physical device or emulator.

🧪 Testing and Compatibility

The application has undergone stress and performance testing in real-world environments, ensuring stability on operating systems with advanced security configurations.
Main Testing Environment

    Device: Google Pixel 10 PRO (Reference hardware for GrapheneOS).

    OS: GrapheneOS (Based on Android 16, Build 2026021201).

    Configuration: De-Googled (Sandboxed Play Services optional).

Verified Points on GrapheneOS

    Permission Management: Correct handling of local file access via Storage Access Framework.

    Service Lifecycle: PlaybackService remains stable under GrapheneOS's strict battery and power management policies.

    Privacy: The app requires no internet connection or telemetry, aligning with the OS's core principles.

    Media Notifications: Lock screen and Quick Settings controls respond with minimal latency.

🛠️ Project Status and Roadmap

You can track pending tasks and identified bugs in our Issues section.
⚖️ License

This project is licensed under the MIT License. See the LICENSE file for more details.
👨‍💻 Author

Daniel Cano Nicolau Java Developer specializing in Web applications | Exploring the Android ecosystem with Kotlin and Jetpack Compose.

    [!TIP]
    If you find a bug not listed in the Issues section, please feel free to open a new one!
