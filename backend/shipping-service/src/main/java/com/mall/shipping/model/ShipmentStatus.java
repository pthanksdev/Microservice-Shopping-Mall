package com.mall.shipping.model;

public enum ShipmentStatus {
    PENDING,
    LABEL_CREATED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED_DELIVERY,
    RETURNED
}
