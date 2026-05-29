package com.mall.admin.service;

import com.mall.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AdminUserService — in a real implementation, this would call user-service
 * via a Feign client or WebClient. Here it demonstrates the service contract.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PagedResponse<Map<String, Object>> listUsers(int page, int size, String search, String status) {
        // TODO: Call user-service via Feign/WebClient with admin credentials
        log.info("Admin listing users - page: {}, size: {}, search: {}, status: {}", page, size, search, status);
        return PagedResponse.<Map<String, Object>>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .build();
    }

    public Map<String, Object> getUserDetails(String userId) {
        // TODO: Aggregate data from multiple services
        log.info("Admin fetching user details: {}", userId);
        return Map.of("userId", userId, "message", "Aggregate view from multiple services");
    }

    public void banUser(String userId, String reason) {
        log.info("Admin banning user: {} for reason: {}", userId, reason);
        kafkaTemplate.send("admin.user.banned", Map.of("userId", userId, "reason", reason));
        // TODO: Call user-service to update status to BANNED
    }

    public void unbanUser(String userId) {
        log.info("Admin unbanning user: {}", userId);
        kafkaTemplate.send("admin.user.unbanned", Map.of("userId", userId));
        // TODO: Call user-service to restore status
    }

    public void deleteUser(String userId) {
        log.info("Admin hard-deleting user: {}", userId);
        // TODO: Cascade delete across services or anonymize data
    }
}
