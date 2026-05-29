package com.mall.admin.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.admin.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin — Products", description = "Product moderation back-office")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "List all products (ADMIN only)")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String vendorId) {
        return ResponseEntity.ok(ApiResponse.success(adminProductService.listProducts(page, size, status, vendorId)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove a product (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> removeProduct(@PathVariable String productId,
                                                            @RequestParam String reason) {
        adminProductService.removeProduct(productId, reason);
        return ResponseEntity.ok(ApiResponse.success("Product removed", null));
    }

    @PostMapping("/{productId}/flag")
    @Operation(summary = "Flag a product for review (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> flagProduct(@PathVariable String productId,
                                                          @RequestParam String reason) {
        adminProductService.flagProduct(productId, reason);
        return ResponseEntity.ok(ApiResponse.success("Product flagged", null));
    }
}
