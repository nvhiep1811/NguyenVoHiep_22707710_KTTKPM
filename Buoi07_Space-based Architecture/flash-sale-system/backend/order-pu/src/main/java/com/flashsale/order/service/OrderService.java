package com.flashsale.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.order.dto.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Order Service - orchestrates the checkout flow.
 * 
 * Flow:
 * 1. Get cart from Redis
 * 2. Get product details from Redis  
 * 3. Call Inventory PU to decrease stock (atomic Lua script)
 * 4. If success → publish order event to Redis List → clear cart
 * 5. Return response immediately (no MongoDB write in hot path)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    @Value("${inventory.service.url:http://localhost:8084}")
    private String inventoryServiceUrl;

    public CheckoutResponse checkout(String userId) {
        try {
            // Step 1: Get cart from Redis
            String cartKey = "cart:" + userId;
            Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(cartKey);

            if (cartEntries.isEmpty()) {
                return CheckoutResponse.builder()
                    .status("FAILED")
                    .message("Cart is empty")
                    .build();
            }

            // Step 2: Build order items from cart + product details from Redis
            List<Map<String, Object>> orderItems = new ArrayList<>();
            List<Map<String, Object>> stockItems = new ArrayList<>();
            double totalAmount = 0;

            for (Map.Entry<Object, Object> entry : cartEntries.entrySet()) {
                String productId = (String) entry.getKey();
                int quantity = Integer.parseInt((String) entry.getValue());

                // Get product detail from Redis
                String productJson = redisTemplate.opsForValue().get("product:" + productId);
                if (productJson == null) {
                    return CheckoutResponse.builder()
                        .status("FAILED")
                        .message("Product not found: " + productId)
                        .build();
                }

                Map<String, Object> product = objectMapper.readValue(productJson, new TypeReference<>() {});
                double price = ((Number) product.get("salePrice")).doubleValue();
                String productName = (String) product.get("name");
                double subtotal = price * quantity;
                totalAmount += subtotal;

                orderItems.add(Map.of(
                    "productId", productId,
                    "productName", productName,
                    "quantity", quantity,
                    "price", price,
                    "subtotal", subtotal
                ));

                stockItems.add(Map.of(
                    "productId", productId,
                    "quantity", quantity
                ));
            }

            // Step 3: Call Inventory PU to decrease stock atomically
            Map<String, Object> decreaseRequest = Map.of("items", stockItems);
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(
                    inventoryServiceUrl + "/stock/decrease",
                    decreaseRequest,
                    Map.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map body = response.getBody();
                    if (body != null && "FAILED".equals(body.get("status"))) {
                        return CheckoutResponse.builder()
                            .status("FAILED")
                            .message("Out of stock")
                            .build();
                    }
                }
            } catch (Exception e) {
                log.error("Failed to call Inventory PU", e);
                return CheckoutResponse.builder()
                    .status("FAILED")
                    .message("Out of stock")
                    .build();
            }

            // Step 4: Generate order code
            String orderId = "order_" + UUID.randomUUID().toString().substring(0, 8);
            String orderCode = "ORD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", orderCounter.getAndIncrement());

            // Step 5: Publish order event to Redis List (async processing by Sync Worker)
            Map<String, Object> orderEvent = new HashMap<>();
            orderEvent.put("orderId", orderId);
            orderEvent.put("orderCode", orderCode);
            orderEvent.put("userId", userId);
            orderEvent.put("items", orderItems);
            orderEvent.put("totalAmount", totalAmount);
            orderEvent.put("timestamp", System.currentTimeMillis());

            String eventJson = objectMapper.writeValueAsString(orderEvent);
            redisTemplate.opsForList().leftPush("order:events", eventJson);
            log.info("📤 Published order event: {}", orderCode);

            // Step 6: Clear cart
            redisTemplate.delete(cartKey);
            log.info("🗑️ Cleared cart for user {}", userId);

            // Step 7: Return success immediately
            return CheckoutResponse.builder()
                .orderCode(orderCode)
                .status("SUCCESS")
                .message("Checkout successfully")
                .totalAmount(totalAmount)
                .build();

        } catch (Exception e) {
            log.error("Checkout failed for user {}", userId, e);
            return CheckoutResponse.builder()
                .status("FAILED")
                .message("Checkout failed: " + e.getMessage())
                .build();
        }
    }
}
