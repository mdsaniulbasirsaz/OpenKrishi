# 🌾 OpenKrishi Customer Authentication Module

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-blue.svg)](https://jwt.io/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-red.svg)](https://spring.io/projects/spring-security)


---

This module manages the complete customer lifecycle from registration to authentication using **Spring Boot**, **Spring Security**, **JWT**, and **JPA**. The system securely stores customer details with location coordinates, validates user input, and issues JWT tokens for stateless authentication.

### Key Features
- ✅ Secure customer registration with validation
- ✅ JWT-based authentication
- ✅ Location tracking (latitude/longitude)
- ✅ Bangladeshi phone number validation
- ✅ Password encryption
- ✅ Stateless authentication filter

---

## 📁 File Architecture

```
├── entity/
│   └── Customer.java                    # JPA Entity
├── dtos/
│   ├── CustomerRegisterDto.java         # Registration DTO
│   ├── CustomerLoginDto.java            # Login DTO
│   └── AuthResponseDto.java             # Authentication Response DTO
├── repository/
│   └── CustomerRepository.java          # JPA Repository
├── services/
│   ├── CustomerAuthService.java         # Authentication Business Logic
│   ├── JwtService.java                  # JWT Token Management
│   └── JwtAuthenticationFilter.java     # Security Filter
└── controller/
    └── CustomerAuthController.java      # REST API Controller
```

---

## 🔍 Detailed File Descriptions

### 📊 Entity Layer

#### `Customer.java`
JPA entity representing customers in the database.

| Field        | Type           | Description                    | Constraints        |
|--------------|----------------|--------------------------------|--------------------|
| `id`         | `Long`         | Primary key                    | Auto-generated     |
| `fullName`   | `String`       | Customer's full name           | Not null           |
| `email`      | `String`       | Email address                  | Not null, Unique   |
| `password`   | `String`       | Encrypted password             | Not null           |
| `phone`      | `String`       | Phone number                   | Optional           |
| `latitude`   | `Double`       | Location coordinate            | Optional           |
| `longitude`  | `Double`       | Location coordinate            | Optional           |
| `createdAt`  | `LocalDateTime`| Creation timestamp             | Auto-set           |

**Key Features:**
- Uses `@PrePersist` to auto-set creation timestamp
- Lombok annotations for boilerplate reduction
- Location tracking capability

---

### 📦 DTO Layer

#### `CustomerRegisterDto.java`
Handles customer registration input with comprehensive validation.

| Field        | Validation Rules                                    | Required |
|--------------|---------------------------------------------------|----------|
| `fullName`   | `@NotBlank`                                       | ✅       |
| `email`      | `@NotBlank`, `@Email`                             | ✅       |
| `password`   | `@NotBlank`, `@Size(min=6)`                       | ✅       |
| `phone`      | `@NotBlank`, `@Pattern` (Bangladeshi format)      | ✅       |
| `latitude`   | No validation                                     | ❌       |
| `longitude`  | No validation                                     | ❌       |

**Phone Validation Pattern:** `^(\+88)?01[3-9]\d{8}$`

#### `CustomerLoginDto.java`
Handles customer login credentials.

| Field      | Validation Rules                    | Required |
|------------|-------------------------------------|----------|
| `email`    | `@NotBlank`, `@Email`              | ✅       |
| `password` | `@NotBlank`, `@Size(min=6)`        | ✅       |

#### `AuthResponseDto.java`
Response object returned after successful authentication.

| Field      | Type     | Description                |
|------------|----------|----------------------------|
| `token`    | `String` | JWT token                  |
| `fullName` | `String` | Customer's full name       |
| `email`    | `String` | Customer's email address   |

---

### 🗂️ Repository Layer

#### `CustomerRepository.java`
Spring Data JPA repository for database operations.

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
```

**Inherited Methods:** `save()`, `findAll()`, `findById()`, `deleteById()`, etc.

---

### 🛠️ Service Layer

#### `CustomerAuthService.java`
Contains business logic for authentication operations.

| Method     | Description                                                          |
|------------|----------------------------------------------------------------------|
| `register` | Validates email uniqueness, encrypts password, saves customer, returns JWT |
| `login`    | Validates credentials, returns JWT on success                       |

**Key Features:**
- Password encryption using `PasswordEncoder`
- Email uniqueness validation
- JWT token generation via `JwtService`

#### `JwtService.java`
Manages JWT token creation, validation, and parsing.

| Method                    | Description                                     |
|---------------------------|-------------------------------------------------|
| `generateToken`           | Creates JWT with email and fullName claims     |
| `validateToken`           | Validates token signature and expiry           |
| `getEmailFromToken`       | Extracts email from token                      |
| `getFullNameFromToken`    | Extracts fullName from token                   |
| `buildAuthResponse`       | Creates AuthResponseDto with fresh JWT         |

**Configuration:**
- Uses environment variables: `JWT_SECRET`, `JWT_EXPIRATION`
- Algorithm: HMAC SHA-256 (HS256)
- Claims: `sub` (email), `fullName`

#### `JwtAuthenticationFilter.java`
Security filter for JWT validation on each request.

**Functionality:**
- Bypasses authentication for `/register` and `/login` endpoints
- Extracts JWT from `Authorization` header
- Validates token and sets Spring Security context
- Handles invalid tokens gracefully

---

### 🚪 Controller Layer

#### `CustomerAuthController.java`
REST API controller exposing authentication endpoints.

**Base URL:** `/v1/api/customer/auth`

| Method | Endpoint    | Request Body            | Response            |
|--------|-------------|-------------------------|---------------------|
| POST   | `/register` | `CustomerRegisterDto`   | `AuthResponseDto`   |
| POST   | `/login`    | `CustomerLoginDto`      | `AuthResponseDto`   |

---

## 🚀 API Endpoints

### 📝 Register Customer

**POST** `/v1/api/customer/auth/register`

```json
{
  "fullName": "",
  "email": "",
  "password": "",
  "phone": "",
  "latitude": ,
  "longitude": 
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "fullName": "",
  "email": ""
}
```

### 🔐 Login Customer

**POST** `/v1/api/customer/auth/login`

```json
{
  "email": "",
  "password": ""
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "fullName": "",
  "email": ""
}
```