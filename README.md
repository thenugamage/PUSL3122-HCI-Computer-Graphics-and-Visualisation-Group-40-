# Lanka Furniture Visualizer

PUSL3122 group coursework: Java Swing desktop application for room and furniture design with 2D/3D visualisation, developed using Agile Scrum practices.

## Database & Accounts

- **Local Storage**: The application uses a local SQLite database (`furniture_visualizer.db`). This file is **not shared** via Git. Accounts created on one computer will not be available on another by default.
- **Cross-Device Access**: To use the same account on different computers:
    1. **Google Sign-in**: Recommend using Google Sign-in. The app will automatically create a local profile for you on any machine when you sign in with the same Google account.
    2. **Manual Sync**: You can manually copy the `furniture_visualizer.db` file between project root directories.

## Quick Start

1. Ensure Java 17 and Maven are installed.
2. Configure `src/main/resources/google_oauth.properties` (see `google_oauth.properties.example`).
3. Run with: `mvn clean compile exec:java`
