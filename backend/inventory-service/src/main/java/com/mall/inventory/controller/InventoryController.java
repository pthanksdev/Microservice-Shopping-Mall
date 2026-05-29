package com.mall.inventory.controller;

import com.mall.common.response.ApiResponse;
import com.mall.inventory.model.InventoryItem;
import com.mall.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock management")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory for a product")
    public ResponseEntity<ApiResponse<InventoryItem>> getInventory(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getByProductId(productId)));
    }

    @PostMapping("/product/{productId}")
    @Operation(summary = "Create or update inventory for a product")
    public ResponseEntity<ApiResponse<InventoryItem>> upsertInventory(
            @PathVariable String productId,
            @RequestParam(required = false) String variantId,
            @RequestParam(required = false) String sku,
            @RequestParam int quantity,
            @RequestParam(required = false, defaultValue = "") String warehouseLocation,
            @RequestParam(defaultValue = "5") int lowStockThreshold) {
        InventoryItem item = inventoryService.createOrUpdate(productId, variantId, sku, quantity,
                warehouseLocation, lowStockThreshold);
        return ResponseEntity.ok(ApiResponse.success("Inventory updated", item));
    }

    @PostMapping("/product/{productId}/reserve")
    @Operation(summary = "Reserve stock for an order")
    public ResponseEntity<ApiResponse<Void>> reserve(@PathVariable String productId,
                                                      @RequestParam int quantity) {
        inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock reserved", null));
    }

    @PostMapping("/product/{productId}/release")
    @Operation(summary = "Release reserved stock")
    public ResponseEntity<ApiResponse<Void>> release(@PathVariable String productId,
                                                      @RequestParam int quantity) {
        inventoryService.releaseReservation(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Reservation released", null));
    }
}
