package com.mall.review.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.review.model.Review;
import com.mall.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Post a new review")
    public ResponseEntity<ApiResponse<Review>> createReview(
            @RequestHeader("X-User-Id") String customerId,
            @RequestBody Review review) {
        review.setCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Review posted", reviewService.createReview(review)));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<ApiResponse<PagedResponse<Review>>> getProductReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, page, size)));
    }

    @PostMapping("/{reviewId}/helpful")
    @Operation(summary = "Vote a review as helpful")
    public ResponseEntity<ApiResponse<Void>> voteHelpful(@PathVariable String reviewId) {
        reviewService.voteHelpful(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Vote recorded", null));
    }
}
