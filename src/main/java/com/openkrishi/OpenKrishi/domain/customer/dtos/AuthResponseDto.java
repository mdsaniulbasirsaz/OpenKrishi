package com.openkrishi.OpenKrishi.domain.customer.dtos;
import lombok.Data;

@Data

public class AuthResponseDto {


    public AuthResponseDto(String token, String fullName, String email)
    {
        this.token = token;
        this.fullName = fullName;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    private String token;
    private String fullName;
    private String email;
}
