# Base image
FROM openjdk:17-jdk-slim

# App directory
WORKDIR /app

# Copy built JAR file (case-sensitive)
COPY build/libs/OpenKrishi-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8085

# Run the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
