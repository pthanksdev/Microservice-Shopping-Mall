package com.mall.discount.controller;

import com.mall.common.response.ApiResponse;
import com.mall.discount.model.Coupon;
import com.mall.discount.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
@Tag(name = "Discounts", description = "Coupon management and validation")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping("/validate")
    @Operation(summary = "Validate a coupon code")
    public ResponseEntity<ApiResponse<Coupon>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(ApiResponse.success(discountService.validateCoupon(code, amount)));
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply a coupon code (increases usage count)")
    public ResponseEntity<ApiResponse<Coupon>> applyCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(ApiResponse.success("Coupon applied", discountService.applyCoupon(code, amount)));
    }

    @PostMapping
    @Operation(summary = "Create a new coupon (Vendor/Admin)")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(ApiResponse.success("Coupon created", discountService.createCoupon(coupon)));
    }
}
