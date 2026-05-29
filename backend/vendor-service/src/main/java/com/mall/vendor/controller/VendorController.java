package com.mall.vendor.controller;

import com.mall.common.response.ApiResponse;
import com.mall.vendor.model.VendorProfile;
import com.mall.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendors", description = "Vendor shop management")
public class VendorController {

    private final VendorService vendorService;

    @GetMapping("/me")
    @Operation(summary = "Get my vendor profile")
    public ResponseEntity<ApiResponse<VendorProfile>> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(vendorService.getByUserId(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vendor by ID (public)")
    public ResponseEntity<ApiResponse<VendorProfile>> getVendor(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(vendorService.getById(id)));
    }

    @PutMapping("/me")
    @Operation(summary = "Update vendor profile")
    public ResponseEntity<ApiResponse<VendorProfile>> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String shopDescription,
            @RequestParam(required = false) String businessEmail,
            @RequestParam(required = false) String businessPhone,
            @RequestParam(required = false) String logoUrl) {
        VendorProfile profile = vendorService.updateProfile(userId, shopName, shopDescription,
                businessEmail, businessPhone, logoUrl);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profile));
    }
}
