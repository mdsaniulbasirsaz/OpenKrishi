package com.openkrishi.OpenKrishi.domain.ngo.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressUpdateRequestDto {

    private String street;
    private String houseNo;
    private String state;
    private String city;
    private String postCode;
    private String village;
}
