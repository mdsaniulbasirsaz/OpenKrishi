# Base image
FROM openjdk:17-jdk-slim

# App directory
WORKDIR /app

# Copy built JAR file
COPY build/libs/openkrishi-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional)
EXPOSE 8085

# Run the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
