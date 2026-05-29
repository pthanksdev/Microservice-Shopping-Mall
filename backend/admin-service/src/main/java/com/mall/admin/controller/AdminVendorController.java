package com.mall.admin.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.admin.service.AdminVendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/vendors")
@RequiredArgsConstructor
@Tag(name = "Admin — Vendors", description = "Vendor moderation and approval back-office")
public class AdminVendorController {

    private final AdminVendorService adminVendorService;

    @GetMapping
    @Operation(summary = "List all vendors with status filter (ADMIN only)")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> listVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(adminVendorService.listVendors(page, size, status)));
    }

    @PostMapping("/{vendorId}/approve")
    @Operation(summary = "Approve a vendor registration (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> approveVendor(@PathVariable String vendorId) {
        adminVendorService.approveVendor(vendorId);
        return ResponseEntity.ok(ApiResponse.success("Vendor approved", null));
    }

    @PostMapping("/{vendorId}/reject")
    @Operation(summary = "Reject a vendor registration (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> rejectVendor(@PathVariable String vendorId,
                                                            @RequestParam String reason) {
        adminVendorService.rejectVendor(vendorId, reason);
        return ResponseEntity.ok(ApiResponse.success("Vendor rejected", null));
    }

    @PostMapping("/{vendorId}/suspend")
    @Operation(summary = "Suspend a vendor (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> suspendVendor(@PathVariable String vendorId,
                                                             @RequestParam String reason) {
        adminVendorService.suspendVendor(vendorId, reason);
        return ResponseEntity.ok(ApiResponse.success("Vendor suspended", null));
    }
}
