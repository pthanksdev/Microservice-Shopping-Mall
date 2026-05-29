package com.mall.product.repository;

import com.mall.product.model.Product;
import com.mall.product.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByVendorId(String vendorId, Pageable pageable);

    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:vendorId IS NULL OR p.vendorId = :vendorId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findWithFilters(
            @Param("categoryId") String categoryId,
            @Param("vendorId") String vendorId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            Pageable pageable);
}
