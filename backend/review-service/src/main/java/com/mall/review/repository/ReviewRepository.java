package com.mall.review.repository;

import com.mall.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByProductIdAndStatusOrderByCreatedAtDesc(String productId, com.mall.review.model.ReviewStatus status, Pageable pageable);
    Page<Review> findByCustomerId(String customerId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId AND r.status = 'PUBLISHED'")
    Double getAverageRating(String productId);
}
