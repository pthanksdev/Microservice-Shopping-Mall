package com.mall.admin.controller;

import com.mall.common.response.ApiResponse;
import com.mall.admin.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin — Dashboard", description = "Platform analytics and KPIs")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get platform-wide statistics (ADMIN only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getPlatformStats()));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue breakdown by period (ADMIN only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenue(
            @RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRevenueBreakdown(period)));
    }
}
