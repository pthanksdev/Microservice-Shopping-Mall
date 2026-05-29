package com.mall.order.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.common.response.PagedResponse;
import com.mall.order.model.*;
import com.mall.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @org.springframework.kafka.annotation.KafkaListener(topics = "payment.succeeded", groupId = "order-group")
    @Transactional
    public void onPaymentSucceeded(Map<String, Object> event) {
        String orderId = (String) event.get("orderId");
        log.info("Payment succeeded for order: {}. Updating status to CONFIRMED.", orderId);
        updateStatus(orderId, OrderStatus.CONFIRMED);
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "payment.failed", groupId = "order-group")
    @Transactional
    public void onPaymentFailed(Map<String, Object> event) {
        String orderId = (String) event.get("orderId");
        log.warn("Payment failed for order: {}. Updating status to CANCELLED.", orderId);
        updateStatus(orderId, OrderStatus.CANCELLED);
    }

    @Transactional
    public Order placeOrder(String customerId, List<OrderItem> items,
                            String shippingAddressId, String couponCode) {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = BigDecimal.valueOf(5.99);
        BigDecimal total = subtotal.add(shippingFee);

        Order order = Order.builder()
                .customerId(customerId)
                .shippingAddressId(shippingAddressId)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .total(total)
                .couponCode(couponCode)
                .status(OrderStatus.AWAITING_PAYMENT)
                .build();

        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        Order saved = orderRepository.save(order);

        kafkaTemplate.send("order.placed", Map.of(
                "orderId", saved.getId(),
                "customerId", customerId,
                "items", items.stream().map(i ->
                        Map.of("productId", i.getProductId(), "quantity", i.getQuantity())).toList()
        ));
        log.info("Order placed: {}", saved.getId());
        return saved;
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    public PagedResponse<Order> getOrdersByCustomer(String customerId, int page, int size) {
        return PagedResponse.of(orderRepository.findByCustomerId(customerId, PageRequest.of(page, size)));
    }

    @Transactional
    public Order updateStatus(String orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        Order updated = orderRepository.save(order);

        String topic = switch (status) {
            case CONFIRMED -> "order.confirmed";
            case CANCELLED -> "order.cancelled";
            default -> null;
        };

        if (topic != null) {
            kafkaTemplate.send(topic, Map.of("orderId", orderId, "customerId", order.getCustomerId()));
        }

        return updated;
    }
}
