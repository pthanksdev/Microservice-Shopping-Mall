package com.mall.vendor.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.vendor.model.VendorProfile;
import com.mall.vendor.model.VendorStatus;
import com.mall.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Automatically creates a PENDING vendor profile when a VENDOR user registers.
     */
    @KafkaListener(topics = "user.registered.vendor", groupId = "vendor-group")
    @Transactional
    public void onVendorUserRegistered(String userId) {
        log.info("Auto-creating vendor profile for new VENDOR user: {}", userId);
        VendorProfile profile = VendorProfile.builder()
                .userId(userId)
                .shopName("My Shop") // placeholder — vendor should update via profile endpoint
                .status(VendorStatus.PENDING)
                .build();
        vendorRepository.save(profile);
    }

    public VendorProfile getByUserId(String userId) {
        return vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("VendorProfile", "userId", userId));
    }

    public VendorProfile getById(String id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VendorProfile", "id", id));
    }

    @Transactional
    public VendorProfile updateProfile(String userId, String shopName, String shopDescription,
                                       String businessEmail, String businessPhone, String logoUrl) {
        VendorProfile profile = getByUserId(userId);
        if (shopName != null) profile.setShopName(shopName);
        if (shopDescription != null) profile.setShopDescription(shopDescription);
        if (businessEmail != null) profile.setBusinessEmail(businessEmail);
        if (businessPhone != null) profile.setBusinessPhone(businessPhone);
        if (logoUrl != null) profile.setLogoUrl(logoUrl);
        return vendorRepository.save(profile);
    }

    @Transactional
    public VendorProfile approveVendor(String id) {
        VendorProfile profile = getById(id);
        profile.setStatus(VendorStatus.APPROVED);
        VendorProfile saved = vendorRepository.save(profile);
        kafkaTemplate.send("vendor.approved", Map.of("vendorId", id, "userId", profile.getUserId()));
        return saved;
    }
}
