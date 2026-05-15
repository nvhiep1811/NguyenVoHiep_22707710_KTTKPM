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
import java.util.List;
import java.util.Map;

/**
 * Order document with embedded order items.
 * order_items nhúng trực tiếp để đọc đơn hàng nhanh.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
@CompoundIndexes({
    @CompoundIndex(name = "idx_user_created", def = "{'userId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "idx_status_created", def = "{'status': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "idx_flashsale_created", def = "{'flashSaleId': 1, 'createdAt': -1}")
})
public class Order {
    @Id
    private String id;

    @Indexed(unique = true)
    private String orderCode;

    private String userId;

    // Snapshot thông tin user tại thời điểm đặt hàng
    private Map<String, String> userSnapshot; // { fullName, phone, email }

    private String flashSaleId;

    private List<OrderItem> items;

    private double totalAmount;

    private String status;         // PENDING, SUCCESS, FAILED, CANCELLED
    private String paymentStatus;  // UNPAID, PAID, REFUNDED
    private String source;         // FLASH_SALE

    @Indexed(unique = true, sparse = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
