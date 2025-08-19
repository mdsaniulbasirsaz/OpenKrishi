package com.openkrishi.OpenKrishi.domain.farmer.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FarmerCreateRequestDto {

    private String farmerName;
    private String phone;
    private Double latitude;
    private Double longitude;
}
