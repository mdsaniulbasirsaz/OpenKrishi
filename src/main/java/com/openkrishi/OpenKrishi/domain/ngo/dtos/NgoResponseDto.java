package com.openkrishi.OpenKrishi.domain.ngo.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NgoResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;

    private String managerName;
    private String licenceUrl;

    private String street;
    private String houseNo;
    private String city;
    private String state;
    private String postCode;
    private String village;
}
