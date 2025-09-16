# Build stage
FROM gradle:8.4-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY src/ src/

RUN ./gradlew clean build -x test

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/OpenKrishi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
