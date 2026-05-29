package com.mall.search.repository;

import com.mall.search.model.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description);
    List<ProductDocument> findByCategory(String category);
}
