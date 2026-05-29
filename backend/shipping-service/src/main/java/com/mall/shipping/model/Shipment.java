package com.mall.shipping.model;

import com.mall.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String orderId;

    private String trackingNumber;
    private String carrier; // e.g. DHL, FedEx, UPS

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PENDING;

    private String originAddress;
    private String destinationAddress;

    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    private String labelUrl;
}
