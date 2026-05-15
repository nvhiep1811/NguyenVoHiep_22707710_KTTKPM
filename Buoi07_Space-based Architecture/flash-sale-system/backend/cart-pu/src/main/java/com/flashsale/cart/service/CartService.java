package com.flashsale.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.cart.dto.CartItemResponse;
import com.flashsale.cart.dto.CartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cart Service - stores cart data entirely in Redis Hash.
 * Key: cart:{userId}, Field: productId, Value: quantity
 * TTL: 30 minutes (1800 seconds)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final long CART_TTL_SECONDS = 1800; // 30 minutes

    public void addToCart(String userId, String productId, int quantity) {
        String cartKey = "cart:" + userId;

        // HINCRBY for atomic increment of quantity
        redisTemplate.opsForHash().increment(cartKey, productId, quantity);

        // Refresh TTL on every cart update
        redisTemplate.expire(cartKey, CART_TTL_SECONDS, TimeUnit.SECONDS);
        log.info("Added {} x {} to cart for user {}", quantity, productId, userId);
    }

    public CartResponse getCart(String userId) {
        String cartKey = "cart:" + userId;
        Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(cartKey);

        List<CartItemResponse> items = new ArrayList<>();
        double totalAmount = 0;

        for (Map.Entry<Object, Object> entry : cartEntries.entrySet()) {
            String productId = (String) entry.getKey();
            int quantity = Integer.parseInt((String) entry.getValue());

            // Get product info from Redis
            String productJson = redisTemplate.opsForValue().get("product:" + productId);
            String productName = productId;
            double price = 0;

            if (productJson != null) {
                try {
                    Map<String, Object> product = objectMapper.readValue(productJson, new TypeReference<>() {});
                    productName = (String) product.get("name");
                    price = ((Number) product.get("salePrice")).doubleValue();
                } catch (Exception e) {
                    log.error("Error parsing product JSON for {}", productId, e);
                }
            }

            double subtotal = price * quantity;
            totalAmount += subtotal;

            items.add(CartItemResponse.builder()
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .subtotal(subtotal)
                .build());
        }

        return CartResponse.builder()
            .userId(userId)
            .items(items)
            .totalAmount(totalAmount)
            .build();
    }

    public void removeFromCart(String userId, String productId) {
        String cartKey = "cart:" + userId;
        redisTemplate.opsForHash().delete(cartKey, productId);
        log.info("Removed {} from cart for user {}", productId, userId);
    }
}
