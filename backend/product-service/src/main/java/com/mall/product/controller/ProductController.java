package com.mall.product.controller;

import com.mall.common.response.ApiResponse;
import com.mall.common.response.PagedResponse;
import com.mall.product.model.Product;
import com.mall.product.model.ProductStatus;
import com.mall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products with optional filters")
    public ResponseEntity<ApiResponse<PagedResponse<Product>>> getProducts(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String vendorId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.getProducts(categoryId, vendorId, minPrice, maxPrice, search, page, size, sortBy)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new product (vendor only)")
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @RequestHeader("X-User-Id") String vendorId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal price,
            @RequestParam String categoryId,
            @RequestParam(required = false) List<String> imageUrls) {
        Product product = productService.createProduct(vendorId, name, description, price, categoryId, imageUrls);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created", product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product (vendor only)")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String vendorId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) ProductStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Product updated",
                productService.updateProduct(id, vendorId, name, description, price, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (soft) a product (vendor only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String vendorId) {
        productService.deleteProduct(id, vendorId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
