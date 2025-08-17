package com.openkrishi.OpenKrishi.domain.user.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class UserAddressDto {

    private UUID addressId;

    private String street;

    private String houseNo;

    private String state;

    private String city;

    private String postCode;

    private String village;
}
