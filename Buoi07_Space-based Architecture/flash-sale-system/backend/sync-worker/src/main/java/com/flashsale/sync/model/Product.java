package com.flashsale.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
@CompoundIndexes({
    @CompoundIndex(name = "idx_status_category", def = "{'status': 1, 'category.id': 1}")
})
public class Product {
    @Id
    private String id;

    @Indexed(unique = true)
    private String sku;

    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;

    private Map<String, String> category;  // { id, name }
    private Map<String, String> brand;     // { id, name }

    private double originalPrice;
    private double salePrice;
    private String thumbnailUrl;
    private List<String> images;
    private Map<String, String> attributes; // { resolution, warranty, origin }

    private String status;   // ACTIVE, INACTIVE

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
