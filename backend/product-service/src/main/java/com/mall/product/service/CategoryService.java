package com.mall.product.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.product.model.Category;
import com.mall.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Transactional
    public Category createCategory(String name, String description, String parentId, String imageUrl) {
        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", parentId));
        }

        return categoryRepository.save(Category.builder()
                .name(name)
                .description(description)
                .imageUrl(imageUrl)
                .parent(parent)
                .build());
    }
}
