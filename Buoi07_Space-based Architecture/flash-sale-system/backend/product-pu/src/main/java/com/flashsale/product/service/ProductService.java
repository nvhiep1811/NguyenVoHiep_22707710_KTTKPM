package com.flashsale.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Product Service - reads ALL data from Redis, NEVER from MongoDB.
 * This is the hot path of the Space-Based Architecture.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ProductResponse> getAllProducts() {
        try {
            String json = redisTemplate.opsForValue().get("products:all");
            if (json == null) {
                log.warn("products:all not found in Redis");
                return Collections.emptyList();
            }

            List<Map<String, Object>> products = objectMapper.readValue(json, new TypeReference<>() {});
            return products.stream().map(p -> {
                String productId = (String) p.get("id");
                String stockStr = redisTemplate.opsForValue().get("stock:" + productId);
                int stock = stockStr != null ? Integer.parseInt(stockStr) : 0;

                return ProductResponse.builder()
                    .id(productId)
                    .name((String) p.get("name"))
                    .description((String) p.get("description"))
                    .originalPrice(((Number) p.get("originalPrice")).doubleValue())
                    .salePrice(((Number) p.get("salePrice")).doubleValue())
                    .thumbnailUrl((String) p.get("thumbnailUrl"))
                    .stock(stock)
                    .build();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error reading products from Redis", e);
            return Collections.emptyList();
        }
    }

    public ProductResponse getProductById(String productId) {
        try {
            String json = redisTemplate.opsForValue().get("product:" + productId);
            if (json == null) {
                return null;
            }

            Map<String, Object> p = objectMapper.readValue(json, new TypeReference<>() {});
            String stockStr = redisTemplate.opsForValue().get("stock:" + productId);
            int stock = stockStr != null ? Integer.parseInt(stockStr) : 0;

            return ProductResponse.builder()
                .id((String) p.get("id"))
                .name((String) p.get("name"))
                .description((String) p.get("description"))
                .originalPrice(((Number) p.get("originalPrice")).doubleValue())
                .salePrice(((Number) p.get("salePrice")).doubleValue())
                .thumbnailUrl((String) p.get("thumbnailUrl"))
                .stock(stock)
                .build();
        } catch (Exception e) {
            log.error("Error reading product {} from Redis", productId, e);
            return null;
        }
    }
}
