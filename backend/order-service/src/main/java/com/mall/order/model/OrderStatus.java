package com.mall.order.model;

public enum OrderStatus {
    PENDING,
    AWAITING_PAYMENT,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
