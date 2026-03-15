# Lanka Furniture Visualizer

PUSL3122 group coursework: Java Swing desktop application for room and furniture design with 2D/3D visualisation, developed using Agile Scrum practices.

## Database & Accounts

- **Shared Storage**: The application's database (`furniture_visualizer.db`) is now **tracked by Git**. This allows the team to share a common set of accounts and projects.
- **Merge Conflicts**: Since the file is shared, please **coordinate with the team** when creating new accounts to avoid database merge conflicts. If a conflict occurs, you may need to choose one version of the database.
- **Google Sign-in**: Still recommended for a seamless cross-device experience.

## Troubleshooting Google Sign-in

If Google Sign-in is not working for team members:

1.  **Check Credentials**: Ensure `src/main/resources/google_oauth.properties` exists on their machine (copy from `.example`).
2.  **Test Users**: If the Google Cloud Project is in "Testing" mode, the owner must add each team member's Google email to the **"Test users"** list in the Google Cloud Console (OAuth consent screen).
3.  **Port 8888**: Ensure port `8888` is not blocked or used by another application on their machine.

## Quick Start

1. Ensure Java 17 and Maven are installed.
2. Configure `src/main/resources/google_oauth.properties` (see `google_oauth.properties.example`).
3. Run with: `mvn clean compile exec:java`
