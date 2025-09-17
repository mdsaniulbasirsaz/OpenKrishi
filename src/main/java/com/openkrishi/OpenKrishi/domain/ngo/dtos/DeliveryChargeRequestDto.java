package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

public class DeliveryChargeRequestDto {
    private UUID ngoId;
    private double amountPerKm;
}
