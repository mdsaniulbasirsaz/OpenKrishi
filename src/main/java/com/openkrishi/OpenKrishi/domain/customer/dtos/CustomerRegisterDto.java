package com.openkrishi.OpenKrishi.domain.customer.dtos;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data

public class CustomerRegisterDto {
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 5 characters long")
    private String password;

    @NotBlank(message = "Phone number is required.")
    @Pattern( regexp = "^(\\+88)?01[3-9]\\d{8}$", message = "Invalid Bangladeshi phone number.")
    private String phone;

    private Double latitude;
    private Double longitude;
}
