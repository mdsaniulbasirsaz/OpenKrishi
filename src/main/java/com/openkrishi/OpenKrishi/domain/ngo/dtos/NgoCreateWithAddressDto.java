package com.openkrishi.OpenKrishi.domain.ngo.dtos;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NgoCreateWithAddressDto {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String managerName;

    private AddressUpdateRequestDto address;
}
