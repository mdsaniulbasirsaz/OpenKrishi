package com.openkrishi.OpenKrishi.domain.auth.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponsoDto {
    private String token;
    private String fullName;
    private String email;
}
