package com.mall.wishlist.controller;

import com.mall.common.response.ApiResponse;
import com.mall.wishlist.model.WishlistItem;
import com.mall.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "User wishlist management")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get my wishlist")
    public ResponseEntity<ApiResponse<List<WishlistItem>>> getMyWishlist(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(userId)));
    }

    @PostMapping
    @Operation(summary = "Add item to wishlist")
    public ResponseEntity<ApiResponse<WishlistItem>> addToWishlist(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody WishlistItem item) {
        item.setUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist", wishlistService.addToWishlist(item)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove item from wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", null));
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if product is in wishlist")
    public ResponseEntity<ApiResponse<Boolean>> isInWishlist(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.isInWishlist(userId, productId)));
    }
}
