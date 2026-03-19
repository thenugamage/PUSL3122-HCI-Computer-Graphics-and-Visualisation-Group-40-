# Lanka Furniture Room Visualizer

A JavaFX-based desktop application for designing and visualizing room layouts with interactive 2D and 3D views. Developed using HCI principles and Agile Scrum methodology.

---

## Download Application

You can download the latest version of the application from the GitHub Releases:

👉 https://github.com/thenugamage/PUSL3122-HCI-Computer-Graphics-and-Visualisation-Group-40-/releases

---

## Available Versions
- **Windows:** `.zip` package (contains executable)  
- **macOS:** `.dmg` file  

---

## How to Run

### Windows
1. Download the `.zip` file  
2. Extract the ZIP file  
3. Open the extracted folder  
4. Run the `.exe` file  

### macOS
1. Download the `.dmg` file  
2. Open the `.dmg` file  
3. Drag the application into the **Applications** folder  
4. Open the app from Applications  

---

## Run from Source Code (For Developers)

### Step 1: Clone or Download the Source Code

#### Option 1: Clone Repository
```bash
git clone https://github.com/thenugamage/PUSL3122-HCI-Computer-Graphics-and-Visualisation-Group-40-.git
cd PUSL3122-HCI-Computer-Graphics-and-Visualisation-Group-40-
```

#### Option 2: Download Source Code
1. Click **Code** → **Download ZIP**
2. Extract the project folder

---

### Step 2: Prerequisites
- **Java JDK 17** or above
- **Maven** installed and configured in your PATH

---

### Step 3: Run the Application

#### Using Maven
Run the following commands in the project root:
```bash
mvn clean install
mvn javafx:run
```

#### Using IDE (Recommended)
1. Open the project in **IntelliJ IDEA**, **VS Code**, or **Eclipse**
2. Import as a **Maven project**
3. Run the main class: `com.capitalcarrier.roomvisualizer.App` (or `MainApp.java`)

---

## Key Features & Technologies
- **JavaFX 17** for rich desktop GUI
- **Interactive 2D & 3D Views** for room layouts
- **Google OAuth Integration** for secure authentication
- **Local SQLite** defaults with support for Remote PostgreSQL