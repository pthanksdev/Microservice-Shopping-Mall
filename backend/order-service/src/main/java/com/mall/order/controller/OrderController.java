package com.mall.order.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.order.model.Order;
import com.mall.order.model.OrderStatus;
import com.mall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order lifecycle management")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get my orders")
    public ResponseEntity<ApiResponse<PagedResponse<Order>>> getMyOrders(
            @RequestHeader("X-User-Id") String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByCustomer(customerId, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", orderService.updateStatus(id, status)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", orderService.updateStatus(id, OrderStatus.CANCELLED)));
    }
}
