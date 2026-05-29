package com.mall.product.controller;

import com.mall.common.response.ApiResponse;
import com.mall.product.model.Category;
import com.mall.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all root categories with children")
    public ResponseEntity<ApiResponse<List<Category>>> getRootCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getRootCategories()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<Category>> getCategory(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryById(id)));
    }

    @PostMapping
    @Operation(summary = "Create category (admin only)")
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String imageUrl) {
        Category cat = categoryService.createCategory(name, description, parentId, imageUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Category created", cat));
    }
}
