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
 * Flash Sale event - tách riêng khỏi flash_sale_items theo thiết kế document.
 * Lưu thông tin chung của chương trình flash sale.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flash_sales")
@CompoundIndexes({
    @CompoundIndex(name = "idx_status_time", def = "{'status': 1, 'startTime': 1, 'endTime': 1}")
})
public class FlashSale {
    @Id
    private String id;
    private String name;
    private String description;

    @Indexed
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status;  // UPCOMING, ACTIVE, ENDED, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
