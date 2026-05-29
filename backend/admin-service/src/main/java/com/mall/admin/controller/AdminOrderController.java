package com.mall.admin.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.admin.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin — Orders", description = "Order management back-office")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "List all orders (ADMIN only)")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId) {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.listOrders(page, size, status, customerId)));
    }

    @PostMapping("/{orderId}/refund")
    @Operation(summary = "Issue a refund for an order (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> refundOrder(@PathVariable String orderId,
                                                          @RequestParam String reason) {
        adminOrderService.refundOrder(orderId, reason);
        return ResponseEntity.ok(ApiResponse.success("Refund initiated", null));
    }

    @PostMapping("/{orderId}/override-status")
    @Operation(summary = "Override order status manually (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> overrideStatus(@PathVariable String orderId,
                                                              @RequestParam String newStatus,
                                                              @RequestParam String reason) {
        adminOrderService.overrideOrderStatus(orderId, newStatus, reason);
        return ResponseEntity.ok(ApiResponse.success("Order status overridden", null));
    }
}
