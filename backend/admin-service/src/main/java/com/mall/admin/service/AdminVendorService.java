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
public class AdminVendorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PagedResponse<Map<String, Object>> listVendors(int page, int size, String status) {
        log.info("Admin listing vendors - status: {}", status);
        // TODO: Call vendor-service with admin token
        return PagedResponse.<Map<String, Object>>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .build();
    }

    public void approveVendor(String vendorId) {
        log.info("Admin approving vendor: {}", vendorId);
        kafkaTemplate.send("vendor.approved", Map.of("vendorId", vendorId));
        // TODO: Call vendor-service to update status to APPROVED
    }

    public void rejectVendor(String vendorId, String reason) {
        log.info("Admin rejecting vendor: {} for: {}", vendorId, reason);
        kafkaTemplate.send("vendor.rejected", Map.of("vendorId", vendorId, "reason", reason));
    }

    public void suspendVendor(String vendorId, String reason) {
        log.info("Admin suspending vendor: {} for: {}", vendorId, reason);
        kafkaTemplate.send("vendor.suspended", Map.of("vendorId", vendorId, "reason", reason));
    }
}
