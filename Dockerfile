# ─────────────────────────────────────────────
# Stage 1: Build with Maven
# ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache dependencies first (only re-downloads when pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────────
# Stage 2: Runtime image (lean JRE)
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre

# Install X11 libraries needed for Swing GUI
RUN apt-get update && apt-get install -y --no-install-recommends \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libx11-6 \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy OAuth credentials (must exist locally — never baked into the image via ARG/ENV)
# Mount at runtime via: -v ./google_oauth.properties:/app/google_oauth.properties
# Or place it inside src/main/resources before building

# The SQLite database file is stored in /app/data (mount a volume to persist it)
RUN mkdir -p /app/data

ENV DISPLAY=:0

# Add /app to classpath so the mounted google_oauth.properties is found by AuthService
ENTRYPOINT ["java", "-cp", "/app:/app/app.jar", "com.capitalcarrier.roomvisualizer.App"]

