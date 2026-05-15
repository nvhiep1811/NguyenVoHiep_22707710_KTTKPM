package com.flashsale.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Flash Sale Item - sản phẩm tham gia flash sale.
 * Tách riêng collection theo thiết kế document (tránh vượt 16MB limit).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flash_sale_items")
@CompoundIndexes({
    @CompoundIndex(name = "idx_flashsale_product", def = "{'flashSaleId': 1, 'productId': 1}", unique = true),
    @CompoundIndex(name = "idx_flashsale_status", def = "{'flashSaleId': 1, 'status': 1}")
})
public class FlashSaleItem {
    @Id
    private String id;

    private String flashSaleId;

    @Indexed
    private String productId;

    // Snapshot sản phẩm tại thời điểm tạo flash sale
    private Map<String, String> productSnapshot; // { name, sku, thumbnailUrl }

    private double flashPrice;
    private int saleStock;
    private int soldCount;
    private int limitPerUser;

    private String status;  // ACTIVE, SOLD_OUT, INACTIVE

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
