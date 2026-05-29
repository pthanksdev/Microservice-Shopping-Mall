package com.mall.discount.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.discount.model.Coupon;
import com.mall.discount.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountService {

    private final CouponRepository couponRepository;

    public Coupon validateCoupon(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));

        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon has expired");
        }

        if (coupon.getStartsAt() != null && coupon.getStartsAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Coupon is not active yet");
        }

        if (coupon.getUsageLimit() != -1 && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit reached");
        }

        if (orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new RuntimeException("Order amount is below the minimum required for this coupon: " + coupon.getMinimumOrderAmount());
        }

        return coupon;
    }

    @Transactional
    public Coupon applyCoupon(String code, BigDecimal orderAmount) {
        Coupon coupon = validateCoupon(code, orderAmount);
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}
