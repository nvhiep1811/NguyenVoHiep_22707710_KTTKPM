package com.flashsale.sync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.sync.model.FlashSaleItem;
import com.flashsale.sync.model.InventorySnapshot;
import com.flashsale.sync.model.Product;
import com.flashsale.sync.repository.FlashSaleItemRepository;
import com.flashsale.sync.repository.InventorySnapshotRepository;
import com.flashsale.sync.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads data from MongoDB Atlas into Redis on startup.
 * This is the key component of the Space-Based Architecture:
 * all hot-path data lives in Redis (the Data Grid).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class DataLoaderService implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        loadProductsToRedis();
        loadStockToRedis();
        loadFlashSaleItemsToRedis();
        log.info("✅ All data loaded from MongoDB Atlas to Redis successfully!");
    }

    private void loadProductsToRedis() throws Exception {
        List<Product> products = productRepository.findAll();

        // Load individual products
        for (Product product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("sku", product.getSku());
            productMap.put("name", product.getName());
            productMap.put("slug", product.getSlug());
            productMap.put("description", product.getDescription());
            productMap.put("originalPrice", product.getOriginalPrice());
            productMap.put("salePrice", product.getSalePrice());
            productMap.put("thumbnailUrl", product.getThumbnailUrl());
            productMap.put("status", product.getStatus());
            if (product.getCategory() != null) productMap.put("category", product.getCategory());
            if (product.getBrand() != null) productMap.put("brand", product.getBrand());

            String productJson = objectMapper.writeValueAsString(productMap);
            redisTemplate.opsForValue().set("product:" + product.getId(), productJson);
            log.info("  Loaded product:{} to Redis", product.getId());
        }

        // Load all products list
        List<Map<String, Object>> productList = products.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("sku", p.getSku());
            m.put("name", p.getName());
            m.put("description", p.getDescription());
            m.put("originalPrice", p.getOriginalPrice());
            m.put("salePrice", p.getSalePrice());
            m.put("thumbnailUrl", p.getThumbnailUrl());
            m.put("status", p.getStatus());
            return m;
        }).collect(Collectors.toList());

        String allProductsJson = objectMapper.writeValueAsString(productList);
        redisTemplate.opsForValue().set("products:all", allProductsJson);
        log.info("✅ Loaded {} products to Redis (products:all)", products.size());
    }

    private void loadStockToRedis() {
        List<InventorySnapshot> snapshots = inventorySnapshotRepository.findAll();
        for (InventorySnapshot snapshot : snapshots) {
            redisTemplate.opsForValue().set(
                "stock:" + snapshot.getProductId(),
                String.valueOf(snapshot.getAvailableStock())
            );
            log.info("  Loaded stock:{} = {} to Redis", snapshot.getProductId(), snapshot.getAvailableStock());
        }
        log.info("✅ Loaded {} stock entries to Redis", snapshots.size());
    }

    private void loadFlashSaleItemsToRedis() throws Exception {
        List<FlashSaleItem> items = flashSaleItemRepository.findAll();
        for (FlashSaleItem item : items) {
            // Also load flash sale specific stock if needed
            String key = "flashsale:" + item.getFlashSaleId() + ":product:" + item.getProductId();
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProductId());
            itemMap.put("flashPrice", item.getFlashPrice());
            itemMap.put("saleStock", item.getSaleStock());
            itemMap.put("limitPerUser", item.getLimitPerUser());
            itemMap.put("status", item.getStatus());
            if (item.getProductSnapshot() != null) itemMap.put("productSnapshot", item.getProductSnapshot());

            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(itemMap));
        }
        log.info("✅ Loaded {} flash sale items to Redis", items.size());
    }
}
