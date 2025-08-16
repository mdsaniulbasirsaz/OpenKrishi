package com.openkrishi.OpenKrishi.domain.auth.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponsoDto {
    private String token;
    private String fullName;
    private String email;
    private String userId;


}
