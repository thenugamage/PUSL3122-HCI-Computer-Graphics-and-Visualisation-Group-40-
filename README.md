# Lanka Furniture Visualizer

PUSL3122 group coursework: Java Swing desktop application for room and furniture design with 2D/3D visualisation, developed using Agile Scrum practices.

## Remote Database & Team Collaboration

The application now uses a **Remote PostgreSQL Database (Supabase)**. This ensures that the entire team shares the same accounts, projects, and furniture data in real-time, eliminating the need for Git-based database synchronisation.

- **IPv4 Compatibility**: We use the Supabase Transaction Pooler (**Port 6543**) to ensure the app works on all networks, including those without IPv6 support.
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
    - **Port 6543**: Ensure you are using port `6543` in the connection string if you are on an IPv4-only network (highly recommended).
2.  **Google Sign-in Issues**:
    - Ensure `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are correctly set.
    - New team members must be added as **"Test users"** in the Google Cloud Console's OAuth consent screen by the project owner.

## Installation & Requirements

To run this project on a new machine, ensure you have the following installed:

1.  **Java Development Kit (JDK) 17**: This project targets Java 17. Check with `java -version`.
2.  **Apache Maven**: Used for dependency management. Check with `mvn -version`.
3.  **Git**: For version control.
4.  **Active Internet Connection**: Required on the first run to download dependencies and consistently for remote database access.

### Getting Started

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/thenugamage/PUSL3122-HCI-Computer-Graphics-and-Visualisation-Group-40-.git
    cd PUSL3122-HCI-Computer-Graphics-and-Visualisation-Group-40-
    ```
2.  **Configuration**: 
    - Copy `src/main/resources/google_oauth.properties.example` to `src/main/resources/google_oauth.properties`.
    - Fill in the `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, and `REMOTE_DB_URL` provided by the team lead.
3.  **Run the Application**:
    ```bash
    mvn javafx:run
    ```

## Development Commands

- **Build Project**: `mvn clean install`
- **Run Tests**: `mvn test`
- **Run App (via JavaFX plugin)**: `mvn javafx:run`
- **Run Performance Tests**: Log output will appear in the console during runtime showing FPS and Memory usage.

