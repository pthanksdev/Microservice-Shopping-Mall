package com.mall.search.service;

import com.mall.search.model.ProductDocument;
import com.mall.search.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductSearchRepository searchRepository;

    @KafkaListener(topics = "product.created", groupId = "search-group")
    public void onProductCreated(Map<String, Object> event) {
        log.info("Indexing new product: {}", event.get("id"));
        ProductDocument doc = ProductDocument.builder()
                .id((String) event.get("id"))
                .name((String) event.get("name"))
                .description((String) event.get("description"))
                .category((String) event.get("category"))
                // Map other fields from event...
                .build();
        searchRepository.save(doc);
    }

    @KafkaListener(topics = "product.updated", groupId = "search-group")
    public void onProductUpdated(Map<String, Object> event) {
        log.info("Updating indexed product: {}", event.get("id"));
        searchRepository.findById((String) event.get("id")).ifPresent(doc -> {
            if (event.containsKey("name")) doc.setName((String) event.get("name"));
            if (event.containsKey("description")) doc.setDescription((String) event.get("description"));
            // Update other fields...
            searchRepository.save(doc);
        });
    }

    public List<ProductDocument> search(String query) {
        return searchRepository.findByNameContainingOrDescriptionContaining(query, query);
    }

    public List<ProductDocument> getByCategory(String category) {
        return searchRepository.findByCategory(category);
    }
}
