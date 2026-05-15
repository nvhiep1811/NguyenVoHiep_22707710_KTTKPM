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
 * Lịch sử thay đổi tồn kho để audit, rollback, thống kê.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stock_movements")
@CompoundIndexes({
    @CompoundIndex(name = "idx_product_created", def = "{'productId': 1, 'createdAt': -1}")
})
public class StockMovement {
    @Id
    private String id;

    private String productId;
    private String flashSaleId;

    @Indexed
    private String orderId;
    private String orderCode;

    private String movementType;  // IMPORT, SALE, CANCEL, ROLLBACK, SYNC
    private int quantity;         // SALE lưu số âm, ví dụ -1
    private int beforeStock;
    private int afterStock;

    private String source;        // REDIS, ADMIN, SYNC_WORKER

    @Indexed(unique = true, sparse = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;
}
