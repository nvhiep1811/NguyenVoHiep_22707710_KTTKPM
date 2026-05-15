package com.flashsale.sync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.sync.model.*;
import com.flashsale.sync.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Async worker that consumes order events from Redis List
 * and persists them to MongoDB Atlas (orders + stock_movements + inventory_snapshots).
 * This decouples the hot checkout path from slow MongoDB writes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final StringRedisTemplate redisTemplate;
    private final OrderRepository orderRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Polls the order:events Redis List every 2 seconds.
     * RPOP ensures FIFO processing.
     */
    @Scheduled(fixedDelay = 2000)
    public void consumeOrderEvents() {
        while (true) {
            String eventJson = redisTemplate.opsForList().rightPop("order:events");
            if (eventJson == null) {
                break; // No more events
            }
            try {
                processOrderEvent(eventJson);
            } catch (Exception e) {
                log.error("❌ Failed to process order event: {}", e.getMessage(), e);
                // Push back to a dead-letter queue for retry
                redisTemplate.opsForList().leftPush("order:events:dlq", eventJson);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processOrderEvent(String eventJson) throws Exception {
        Map<String, Object> event = objectMapper.readValue(eventJson, Map.class);
        String orderId = (String) event.get("orderId");
        String orderCode = (String) event.get("orderCode");
        String userId = (String) event.get("userId");
        double totalAmount = ((Number) event.get("totalAmount")).doubleValue();
        List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
        String flashSaleId = (String) event.getOrDefault("flashSaleId", "flash_sale_1");

        // 1. Save order to MongoDB Atlas
        List<OrderItem> orderItems = items.stream().map(item -> OrderItem.builder()
            .productId((String) item.get("productId"))
            .productName((String) item.get("productName"))
            .sku((String) item.getOrDefault("sku", ""))
            .thumbnailUrl((String) item.getOrDefault("thumbnailUrl", ""))
            .quantity(((Number) item.get("quantity")).intValue())
            .price(((Number) item.get("price")).doubleValue())
            .subtotal(((Number) item.get("subtotal")).doubleValue())
            .build()
        ).toList();

        String idempotencyKey = "checkout-" + userId + "-" + orderId;

        Order order = Order.builder()
            .id(orderId)
            .orderCode(orderCode)
            .userId(userId)
            .userSnapshot(Map.of("fullName", "User " + userId))
            .flashSaleId(flashSaleId)
            .items(orderItems)
            .totalAmount(totalAmount)
            .status("SUCCESS")
            .paymentStatus("UNPAID")
            .source("FLASH_SALE")
            .idempotencyKey(idempotencyKey)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        orderRepository.save(order);
        log.info("✅ Saved order {} to MongoDB Atlas", orderCode);

        // 2. Save stock movements to MongoDB Atlas
        for (Map<String, Object> item : items) {
            String productId = (String) item.get("productId");
            int quantity = ((Number) item.get("quantity")).intValue();

            // Get current stock from Redis for after-stock calculation
            String stockStr = redisTemplate.opsForValue().get("stock:" + productId);
            int afterStock = stockStr != null ? Integer.parseInt(stockStr) : 0;

            String movementIdempotencyKey = "stock-" + orderCode + "-" + productId;

            StockMovement movement = StockMovement.builder()
                .id("movement_" + UUID.randomUUID().toString().substring(0, 8))
                .productId(productId)
                .flashSaleId(flashSaleId)
                .orderId(orderId)
                .orderCode(orderCode)
                .movementType("SALE")
                .quantity(-quantity)
                .beforeStock(afterStock + quantity)
                .afterStock(afterStock)
                .source("REDIS")
                .idempotencyKey(movementIdempotencyKey)
                .createdAt(LocalDateTime.now())
                .build();

            stockMovementRepository.save(movement);
            log.info("  📦 Stock movement: {} qty={} stock={}→{}", productId, -quantity, afterStock + quantity, afterStock);

            // 3. Update inventory snapshot in MongoDB Atlas
            InventorySnapshot snapshot = inventorySnapshotRepository.findAll().stream()
                .filter(s -> s.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

            if (snapshot != null) {
                snapshot.setAvailableStock(afterStock);
                snapshot.setSoldStock(snapshot.getTotalStock() - afterStock);
                snapshot.setLastSyncAt(LocalDateTime.now());
                snapshot.setUpdatedAt(LocalDateTime.now());
                inventorySnapshotRepository.save(snapshot);
            }
        }

        // 4. Save outbox event for audit trail
        OutboxEvent outboxEvent = OutboxEvent.builder()
            .id("evt_" + UUID.randomUUID().toString().substring(0, 8))
            .eventType("ORDER_CREATED")
            .aggregateType("ORDER")
            .aggregateId(orderCode)
            .payload(Map.of("orderCode", orderCode, "userId", userId, "totalAmount", totalAmount))
            .status("DONE")
            .retryCount(0)
            .createdAt(LocalDateTime.now())
            .processedAt(LocalDateTime.now())
            .build();
        outboxEventRepository.save(outboxEvent);

        log.info("✅ Order event processed completely for {}", orderCode);
    }
}
