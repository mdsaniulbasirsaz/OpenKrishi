package com.openkrishi.OpenKrishi.domain.ngo.entity;


import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Getter
    @Setter
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Getter
    @Setter
    @Column(nullable = false)
    private BigDecimal value;

    @Getter
    @Setter
    @Column(nullable = false)
    private String unit;

    @Getter
    @Setter
    private String season;


    @Getter
    @Setter
    @Column(length = 500)
    private String description;


    @Getter
    @Setter
    @Column(name = "local_price", nullable = false)
    private BigDecimal localPrice;


    @Setter
    @Getter
    @Column(name = "market_price", nullable = false)
    private BigDecimal marketPrice;

    @Getter
    @Setter
    private Double discount;

    @Setter
    @Getter
    private String productImage;




    @Getter
    @Setter
    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;



    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;



    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private Ngo ngo;








}
