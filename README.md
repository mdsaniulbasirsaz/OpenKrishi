# OpenKrishi

OpenKrishi is a modular, extensible platform built with Java Spring Boot to support agricultural innovation, data-driven decision-making, and digital services for farmers, NGOs, customers, and administrators. The project is organized using Domain-Driven Design (DDD) principles and is ready for rapid development and deployment.

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Helpful Links](#helpful-links)
- [Contributing](#contributing)
- [License](#license)

## Features
- Modular DDD structure for extensibility
- RESTful APIs with Spring Web
- Data persistence with Spring Data JPA (PostgreSQL)
- AI modules for forecasting and recommendations
- Geolocation services
- Scheduled jobs (cron)
- OpenAPI/Swagger documentation

## Project Structure
```
openkrishi/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/openkrishi/
│       │       ├── OpenKrishiApplication.java       ← Main class
│       │       ├── config/      ← App-level configs (Beans, Swagger, Security)
│       │       ├── common/      ← Shared utilities
│       │       ├── domain/      ← DDD layer (farmer, ngo, customer, admin)
│       │       ├── ai/          ← AI logic
│       │       ├── geo/         ← Geolocation services
│       │       └── scheduler/   ← Scheduled jobs
│       └── resources/
│           ├── application.properties
│           ├── static/
│           └── templates/
└── build.gradle
```

## Prerequisites
- **Java JDK 17** or higher
- **Gradle** (wrapper included, no need to install globally)
- **Git**
- An IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Setup Instructions
1. **Clone the Repository**
   ```sh
   git clone <repository-url>
   cd OpenKrishi
   ```

2. **Configure Environment Variables (Optional)**
   - Edit `src/main/resources/application.properties` or set environment variables as needed.

3. **Build the Project**
   - Using the Gradle wrapper:
     ```sh
     ./gradlew build
     ```
     On Windows:
     ```sh
     gradlew.bat build
     ```
   - To skip tests during build:
     ```sh
     ./gradlew build -x test
     ```

4. **Run the Application**
   - Using the Gradle wrapper:
     ```sh
     ./gradlew bootRun
     ```
     On Windows:
     ```sh
     gradlew.bat bootRun
     ```

5. **Access the Application**
   - By default, the application runs at: the app runs at [http://localhost:PORT/swagger-ui/index.html](http://localhost:PORT/swagger-ui/index.html).

## Running Tests
```sh
./gradlew test
```
On Windows:
```sh
gradlew.bat test
```

## Configuration
- All configuration files are in `src/main/resources/`.
- Static files and templates are in their respective folders under `resources/`.
- Default application properties:
  - `spring.application.name=OpenKrishi`
  - `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`
- Database: PostgreSQL (ensure a running instance and update properties as needed)

## Helpful Links
- [Official Gradle documentation](https://docs.gradle.org)
- [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.3/gradle-plugin)
- [Spring Web](https://docs.spring.io/spring-boot/3.5.3/reference/web/servlet.html)
- [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

## Contributing
Contributions are welcome! Please fork the repository, create a feature branch, and submit a pull request. For major changes, open an issue first to discuss what you would like to change.

## License
This project is licensed under the MIT License. 