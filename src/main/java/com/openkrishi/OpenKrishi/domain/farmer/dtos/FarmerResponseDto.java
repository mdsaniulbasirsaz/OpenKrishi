package com.openkrishi.OpenKrishi.domain.farmer.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class FarmerResponseDto {
    private UUID id;
    private String farmerName;
    private String phone;
    private Double latitude;
    private Double longitude;
    private UUID createdByMemberId;
    private String createdByMemberName;
    private UUID ngoId;
    private String ngoManagerName;
}
