package com.mall.inventory.model;

import com.mall.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String productId;

    private String variantId;
    private String sku;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantityAvailable = 0;

    @Builder.Default
    private Integer quantityReserved = 0;

    private Integer lowStockThreshold;
    private String warehouseLocation;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StockStatus status = StockStatus.IN_STOCK;

    public int getQuantityOnHand() {
        return quantityAvailable - quantityReserved;
    }
}
