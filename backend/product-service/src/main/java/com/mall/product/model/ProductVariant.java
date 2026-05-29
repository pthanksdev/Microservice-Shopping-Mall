package com.mall.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String sku;
    private String size;
    private String color;
    private String material;

    @Column(precision = 10, scale = 2)
    private BigDecimal additionalPrice;

    private Integer stockQuantity;
}
