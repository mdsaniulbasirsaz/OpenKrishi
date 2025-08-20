package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Data
@Setter
public class ProductResponseDto {
    private UUID id;
    private String productName;
    private BigDecimal value;
    private String unit;
    private String season;
    private String description;
    private BigDecimal localPrice;
    private BigDecimal marketPrice;
    private Double discount;
    private Boolean isAvailable;
    private String productImage;
    private String categoryName;
    private String farmerName;
    private String ngoName;

    // Constructor
    public ProductResponseDto(UUID id, String productName, BigDecimal value, String unit, String season,
                              String description, BigDecimal localPrice, BigDecimal marketPrice, Double discount,
                              Boolean isAvailable, String productImage, String categoryName,
                              String farmerName, String ngoName) {
        this.id = id;
        this.productName = productName;
        this.value = value;
        this.unit = unit;
        this.season = season;
        this.description = description;
        this.localPrice = localPrice;
        this.marketPrice = marketPrice;
        this.discount = discount;
        this.isAvailable = isAvailable;
        this.productImage = productImage;
        this.categoryName = categoryName;
        this.farmerName = farmerName;
        this.ngoName = ngoName;
    }
}
