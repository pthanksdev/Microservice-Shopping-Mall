package com.mall.wishlist.model;

import com.mall.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WishlistItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "user_id")
    private String userId;

    @Column(nullable = false, name = "product_id")
    private String productId;

    private String variantId;
    private String productName;
    private String productImageUrl;
    private String vendorId;

    @Builder.Default
    private boolean alertOnSale = true;
}
