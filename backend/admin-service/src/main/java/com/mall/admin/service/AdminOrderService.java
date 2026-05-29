package com.mall.admin.service;

import com.mall.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PagedResponse<Map<String, Object>> listOrders(int page, int size, String status, String customerId) {
        // TODO: Call order-service with admin privileges
        return PagedResponse.<Map<String, Object>>builder()
                .content(List.of()).page(page).size(size).totalElements(0).totalPages(0).build();
    }

    public void refundOrder(String orderId, String reason) {
        log.info("Admin initiating refund for order: {}", orderId);
        kafkaTemplate.send("admin.order.refund", Map.of("orderId", orderId, "reason", reason));
    }

    public void overrideOrderStatus(String orderId, String newStatus, String reason) {
        log.info("Admin overriding order {} status to {}", orderId, newStatus);
        kafkaTemplate.send("admin.order.status.override", Map.of(
                "orderId", orderId, "newStatus", newStatus, "reason", reason));
    }
}
