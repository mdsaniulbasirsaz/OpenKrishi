Customer Profile API - OpenKrishi

This module allows authenticated customers to fetch their profile details securely using their JWT token.

---


## Description


1. Customer logs in and receives a JWT token.
2. Customer sends a GET request to `/api/v1/customer/profile` with the token.
3. Backend extracts email from token and returns the customer's profile.
---

## Endpoint: Get Customer Profile

- **URL**: `/api/v1/customer/profile`
- **Method**: `GET`
- **Auth Required**: Yes (JWT)

###  Request Header

```
Authorization: Bearer <your_jwt_token>
Content-Type: application/json
```

### Sample Response (200 OK)

```json
{
  "fullName": "",
  "email": "",
  "phone": "",
  "latitude":,
  "longitude":,
  "createdAt": "2024-11-24T12:00:00"
}
```

---

##  Error Responses

## Logic Breakdown

```java
@GetMapping("/profile")
public ResponseEntity<CustomerProfileDto> getProfile(Authentication authentication) {
    String email = authentication.getName();
    CustomerProfileDto profile = authService.getProfileByEmail(email);
    return ResponseEntity.ok(profile);
}
```

### 🔹 Service: `CustomerAuthService.java`

```java
public CustomerProfileDto getProfileByEmail(String email) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Customer with email '" + email + "' not found"));
    return CustomerProfileMapper.toDto(customer);
}
```

### 🔹 Mapper: `CustomerProfileMapper.java`

```java
public class CustomerProfileMapper {
    public static CustomerProfileDto toDto(Customer customer) {
        CustomerProfileDto dto = new CustomerProfileDto();
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setLatitude(customer.getLatitude());
        dto.setLongitude(customer.getLongitude());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
}
```

---

## DTO: `CustomerProfileDto.java`

```java
public class CustomerProfileDto {
    private String fullName;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    // Getters and Setters
}
```