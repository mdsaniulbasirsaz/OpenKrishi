package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DeliveryChargeResponseDto {
    private UUID id;
    private double amountPerKm;
    private UUID ngoId;
}
