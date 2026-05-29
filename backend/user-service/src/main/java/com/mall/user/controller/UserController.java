package com.mall.user.controller;

import com.mall.common.response.ApiResponse;
import com.mall.user.dto.UserResponse;
import com.mall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String profileImageUrl) {
        UserResponse updated = userService.updateProfile(userId, firstName, lastName, profileImageUrl);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete (soft) current user account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@RequestHeader("X-User-Id") String userId) {
        userService.softDeleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Account deleted", null));
    }
}
