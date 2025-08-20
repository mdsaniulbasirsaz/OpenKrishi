package com.openkrishi.OpenKrishi.domain.ngo.dtos;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDto {
    private String description;
    private BigDecimal localPrice;
    private BigDecimal marketPrice;
    private Double discount;
    private BigDecimal value;
}
