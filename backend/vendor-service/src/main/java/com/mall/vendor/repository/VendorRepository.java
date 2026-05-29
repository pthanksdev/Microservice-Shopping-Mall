package com.mall.vendor.repository;

import com.mall.vendor.model.VendorProfile;
import com.mall.vendor.model.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorProfile, String> {
    Optional<VendorProfile> findByUserId(String userId);
    Page<VendorProfile> findByStatus(VendorStatus status, Pageable pageable);
}
