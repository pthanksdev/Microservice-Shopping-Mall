package com.mall.product.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.common.response.PagedResponse;
import com.mall.product.model.*;
import com.mall.product.repository.CategoryRepository;
import com.mall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Product createProduct(String vendorId, String name, String description,
                                 BigDecimal price, String categoryId, List<String> imageUrls) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Product product = Product.builder()
                .vendorId(vendorId)
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .imageUrls(imageUrls != null ? imageUrls : List.of())
                .status(ProductStatus.ACTIVE)
                .build();

        product = productRepository.save(product);

        // Publish to Kafka for search indexing + inventory seeding
        kafkaTemplate.send("product.created", Map.of("productId", product.getId(), "vendorId", vendorId));
        log.info("Published product.created event: {}", product.getId());

        return product;
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public PagedResponse<Product> getProducts(String categoryId, String vendorId,
                                               BigDecimal minPrice, BigDecimal maxPrice,
                                               String search, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return PagedResponse.of(productRepository.findWithFilters(categoryId, vendorId, minPrice, maxPrice, search, pageable));
    }

    @Transactional
    public Product updateProduct(String id, String vendorId, String name,
                                 String description, BigDecimal price, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getVendorId().equals(vendorId)) {
            throw new com.mall.common.exception.BusinessException("You don't own this product");
        }

        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (status != null) product.setStatus(status);

        product = productRepository.save(product);
        kafkaTemplate.send("product.updated", Map.of("productId", product.getId()));
        return product;
    }

    @Transactional
    public void deleteProduct(String id, String vendorId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getVendorId().equals(vendorId)) {
            throw new com.mall.common.exception.BusinessException("You don't own this product");
        }

        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }
}
