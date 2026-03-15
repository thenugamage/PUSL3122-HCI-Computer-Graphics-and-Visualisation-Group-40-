# Lanka Furniture Visualizer

PUSL3122 group coursework: Java Swing desktop application for room and furniture design with 2D/3D visualisation, developed using Agile Scrum practices.

## Remote Database & Team Collaboration

The application now uses a **Remote PostgreSQL Database (Supabase)**. This ensures that the entire team shares the same accounts, projects, and furniture data in real-time, eliminating the need for Git-based database synchronisation.

- **Real-time Sync**: Changes made by one team member are immediately visible to others.
- **Persistent Storage**: All designs and user accounts are stored safely in the cloud.

## Authentication System

We have refined the authentication system for a better user experience:
- **Simplified Login**: Sign in using your **Username** and Password (Email is no longer required for local login).
- **Simplified Registration**: The "Full Name" field has been removed to streamline account creation.
- **Validation**:
    - Usernames must be at least 3 characters.
    - Passwords must be at least 6 characters.
    - Email formats are strictly validated during registration.

## Troubleshooting

1.  **Database Connection Failed**:
    - Ensure your `REMOTE_DB_URL` in `src/main/resources/google_oauth.properties` is correct.
    - If you see a "password authentication failed" error, check with the team lead for the latest **Database Password** from the Supabase dashboard.
    - Ensure you are on a network that permits outgoing connections to port `5432`.
2.  **Google Sign-in Issues**:
    - Ensure `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are correctly set.
    - New team members must be added as **"Test users"** in the Google Cloud Console's OAuth consent screen by the project owner.

## Quick Start

1.  **Java Environment**: Ensure Java 17 and Maven are installed.
2.  **Configuration**: 
    - Copy `google_oauth.properties.example` to `src/main/resources/google_oauth.properties`.
    - Fill in the `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, and `REMOTE_DB_URL` provided by the team lead.
3.  **Run**: `mvn clean compile exec:java`
