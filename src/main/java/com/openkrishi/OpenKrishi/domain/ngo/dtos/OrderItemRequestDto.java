package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class OrderItemRequestDto {
    private final UUID productId;
    private final int quantity;
}