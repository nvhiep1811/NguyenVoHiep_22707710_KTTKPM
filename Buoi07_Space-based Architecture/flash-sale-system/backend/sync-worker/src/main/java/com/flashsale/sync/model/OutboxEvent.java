package com.flashsale.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Outbox event - đảm bảo không mất event khi hệ thống gặp lỗi.
 * Checkout trả kết quả nhanh rồi worker xử lý event sau.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "outbox_events")
@CompoundIndexes({
    @CompoundIndex(name = "idx_status_created", def = "{'status': 1, 'createdAt': 1}"),
    @CompoundIndex(name = "idx_aggregate", def = "{'aggregateType': 1, 'aggregateId': 1}")
})
public class OutboxEvent {
    @Id
    private String id;

    private String eventType;       // ORDER_CREATED, STOCK_DECREASED, ORDER_FAILED
    private String aggregateType;   // ORDER, STOCK
    private String aggregateId;

    private Map<String, Object> payload;

    private String status;          // PENDING, PROCESSING, DONE, FAILED
    private int retryCount;
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
