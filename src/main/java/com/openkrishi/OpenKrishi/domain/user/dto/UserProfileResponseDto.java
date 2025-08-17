package com.openkrishi.OpenKrishi.domain.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;
@Data
public class UserProfileResponseDto {

    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private String image;
    private LocalDate dob;
    private UserAddressDto address;
    private String licenceUrl;
    private String managerName;
}
