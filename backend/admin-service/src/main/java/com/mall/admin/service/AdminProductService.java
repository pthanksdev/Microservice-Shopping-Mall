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
public class AdminProductService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PagedResponse<Map<String, Object>> listProducts(int page, int size, String status, String vendorId) {
        // TODO: Call product-service with admin privileges
        return PagedResponse.<Map<String, Object>>builder()
                .content(List.of()).page(page).size(size).totalElements(0).totalPages(0).build();
    }

    public void removeProduct(String productId, String reason) {
        log.info("Admin removing product: {} for: {}", productId, reason);
        kafkaTemplate.send("admin.product.removed", Map.of("productId", productId, "reason", reason));
    }

    public void flagProduct(String productId, String reason) {
        log.info("Admin flagging product: {} for: {}", productId, reason);
        kafkaTemplate.send("admin.product.flagged", Map.of("productId", productId, "reason", reason));
    }
}
