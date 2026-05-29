package com.mall.shipping.controller;

import com.mall.common.response.ApiResponse;
import com.mall.shipping.model.Shipment;
import com.mall.shipping.model.ShipmentStatus;
import com.mall.shipping.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
@Tag(name = "Shipping", description = "Shipment tracking and management")
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get shipment details by order ID")
    public ResponseEntity<ApiResponse<Shipment>> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.getByOrderId(orderId)));
    }

    @PostMapping("/{shipmentId}/label")
    @Operation(summary = "Create shipping label (Carrier simulation)")
    public ResponseEntity<ApiResponse<Shipment>> createLabel(@PathVariable String shipmentId, @RequestParam String carrier) {
        return ResponseEntity.ok(ApiResponse.success("Label created", shippingService.createLabel(shipmentId, carrier)));
    }

    @PatchMapping("/{shipmentId}/status")
    @Operation(summary = "Update shipment status")
    public ResponseEntity<ApiResponse<Shipment>> updateStatus(@PathVariable String shipmentId, @RequestParam ShipmentStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", shippingService.updateStatus(shipmentId, status)));
    }
}
