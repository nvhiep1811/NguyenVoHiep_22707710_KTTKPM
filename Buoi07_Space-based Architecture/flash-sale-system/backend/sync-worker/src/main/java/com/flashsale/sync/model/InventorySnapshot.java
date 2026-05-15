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

/**
 * Inventory snapshot - tồn kho bền vững trong MongoDB.
 * Trong lúc flash sale, stock real-time nằm ở Redis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory_snapshots")
@CompoundIndexes({
    @CompoundIndex(name = "idx_product_flashsale", def = "{'productId': 1, 'flashSaleId': 1}")
})
public class InventorySnapshot {
    @Id
    private String id;

    @Indexed
    private String productId;
    private String flashSaleId;

    private int totalStock;
    private int availableStock;
    private int reservedStock;
    private int soldStock;

    private LocalDateTime lastSyncAt;
    private LocalDateTime updatedAt;
}
