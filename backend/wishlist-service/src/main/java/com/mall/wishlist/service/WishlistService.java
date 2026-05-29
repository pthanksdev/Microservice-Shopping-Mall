package com.mall.wishlist.service;

import com.mall.wishlist.model.WishlistItem;
import com.mall.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public List<WishlistItem> getWishlist(String userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public WishlistItem addToWishlist(WishlistItem item) {
        if (wishlistRepository.existsByUserIdAndProductId(item.getUserId(), item.getProductId())) {
            return wishlistRepository.findByUserIdAndProductId(item.getUserId(), item.getProductId()).get();
        }
        return wishlistRepository.save(item);
    }

    @Transactional
    public void removeFromWishlist(String userId, String productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
