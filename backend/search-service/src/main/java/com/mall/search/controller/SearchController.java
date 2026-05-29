package com.mall.search.controller;

import com.mall.common.response.ApiResponse;
import com.mall.search.model.ProductDocument;
import com.mall.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Elasticsearch-powered product search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search products by name/description")
    public ResponseEntity<ApiResponse<List<ProductDocument>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(searchService.search(q)));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filter products by category")
    public ResponseEntity<ApiResponse<List<ProductDocument>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getByCategory(category)));
    }
}
