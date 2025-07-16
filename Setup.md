# OpenKrishi Project Setup Guide

## Prerequisites

- **Java Development Kit (JDK) 17 or higher**  
  Download from [AdoptOpenJDK](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/).  
  To verify installation, run:  
  ```sh
  java -version
  ```

- **Git**  
  Download from [git-scm.com](https://git-scm.com/).  
  To verify installation, run:  
  ```sh
  git --version
  ```

- **Gradle (Optional)**  
  The project uses the Gradle Wrapper, so you don’t need to install Gradle manually.

- **IDE (Recommended)**  
  IntelliJ IDEA or Eclipse or VsCode.

---

## Setup Steps

1. **Clone the Repository**
   ```sh
   git clone https://github.com/your-org/OpenKrishi.git
   cd OpenKrishi
   ```

2. **Add Root Directory (.env)**
```
PORT=
DB_URL=jdbc:postgresql://localhost:PORT/OpenKrishi
DB_USERNAME=
DB_PASSWORD=
```
3. **Open the Project in Your IDE**  
   - Open the `OpenKrishi` folder as a project.  
   - If prompted, import as a Gradle project.

4. **Configure Java SDK**  
   - Set the project SDK to Java 17 (or higher) in your IDE settings.

5. **Build the Project**  
   Using Gradle Wrapper (Linux/macOS):
   ```sh
   ./gradlew build
   ```
   On Windows:
   ```sh
   gradlew.bat build
   ```

6. **Run the Application**  
   From the command line (Linux/macOS):
   ```sh
   ./gradlew bootRun
   ```
   Or in Windows:
   ```sh
   gradlew.bat bootRun
   ```
   Or, run `OpenKrishiApplication.java` directly from your IDE.

---

## Access the Application

By default, the app runs at [http://localhost:PORT/swagger-ui/index.html](http://localhost:PORT/swagger-ui/index.html).

---
