package com.mall.review.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.common.response.PagedResponse;
import com.mall.review.model.Review;
import com.mall.review.model.ReviewStatus;
import com.mall.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Review createReview(Review review) {
        log.info("Creating review for product: {} by customer: {}", review.getProductId(), review.getCustomerId());
        Review saved = reviewRepository.save(review);
        
        // Notify product service to update average rating
        Double avgRating = reviewRepository.getAverageRating(review.getProductId());
        kafkaTemplate.send("product.rating.updated", Map.of(
                "productId", review.getProductId(),
                "averageRating", avgRating != null ? avgRating : review.getRating().doubleValue()
        ));
        
        return saved;
    }

    public PagedResponse<Review> getProductReviews(String productId, int page, int size) {
        Page<Review> reviewPage = reviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(
                productId, ReviewStatus.PUBLISHED, PageRequest.of(page, size));
        
        return PagedResponse.<Review>builder()
                .content(reviewPage.getContent())
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    @Transactional
    public void voteHelpful(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.setHelpfulVotes(review.getHelpfulVotes() + 1);
        reviewRepository.save(review);
    }
}
