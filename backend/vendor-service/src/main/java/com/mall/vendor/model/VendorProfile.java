package com.mall.vendor.model;

import com.mall.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vendor_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String shopName;

    private String shopDescription;
    private String logoUrl;
    private String businessEmail;
    private String businessPhone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VendorStatus status = VendorStatus.PENDING;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal commissionRate = BigDecimal.valueOf(10.00); // 10%

    private Double rating;
    private Integer totalSales;
}
