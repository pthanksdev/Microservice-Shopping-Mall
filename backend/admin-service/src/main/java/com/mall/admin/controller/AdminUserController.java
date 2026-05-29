package com.mall.admin.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin — Users", description = "User management back-office")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users with pagination (ADMIN only)")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.listUsers(page, size, search, status)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details by ID (ADMIN only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserDetails(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getUserDetails(userId)));
    }

    @PostMapping("/{userId}/ban")
    @Operation(summary = "Ban a user account (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable String userId,
                                                      @RequestParam String reason) {
        adminUserService.banUser(userId, reason);
        return ResponseEntity.ok(ApiResponse.success("User banned", null));
    }

    @PostMapping("/{userId}/unban")
    @Operation(summary = "Unban a user account (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable String userId) {
        adminUserService.unbanUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User unbanned", null));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Hard delete a user (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
