package com.openkrishi.OpenKrishi.domain.ngo.dtos;


import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductCreateDto {
    private String productName;
    private BigDecimal value;
    private String unit;
    private String season;
    private String description;
    private BigDecimal localPrice;
    private BigDecimal marketPrice;
    private Double discount;
    private String productImage;
    private Boolean isAvailable = true;

    // Relationships by IDs
    private UUID categoryId;
    private UUID farmerId;
}
