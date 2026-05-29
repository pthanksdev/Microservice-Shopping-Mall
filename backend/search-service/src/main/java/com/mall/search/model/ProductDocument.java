package com.mall.search.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String vendorId;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Double)
    private Double averageRating;

    @Field(type = FieldType.Boolean)
    private boolean inStock;
}
