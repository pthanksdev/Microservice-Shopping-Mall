package com.mall.discount.model;

import com.mall.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal maximumDiscountAmount;

    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;

    @Builder.Default
    private Integer usageLimit = -1; // -1 = unlimited
    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private boolean active = true;

    private String vendorId; // null = platform-wide
}
