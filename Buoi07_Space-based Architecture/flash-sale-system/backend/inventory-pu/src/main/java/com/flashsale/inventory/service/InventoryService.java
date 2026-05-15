package com.flashsale.inventory.service;

import com.flashsale.inventory.dto.StockItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Inventory Service - uses Redis Lua Script for atomic stock operations.
 * 
 * ANTI-OVERSELL MECHANISM:
 * Instead of the dangerous pattern:
 *   GET stock → if stock > 0 → DECR stock  (race condition!)
 * 
 * We use a Lua script that atomically:
 * 1. Checks ALL stock keys have sufficient quantity
 * 2. If ALL have enough stock → DECRBY all keys
 * 3. If ANY is insufficient → return error, don't decrease anything
 * 
 * This guarantees no oversell even under 1000+ concurrent requests.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Lua Script for atomic multi-product stock decrease.
     * 
     * KEYS = [stock:product_1, stock:product_2, ...]
     * ARGV = [quantity_1, quantity_2, ...]
     * 
     * Returns 1 if success, -1 if any product has insufficient stock.
     */
    private static final String DECREASE_STOCK_LUA_SCRIPT =
        "-- Step 1: Check all products have sufficient stock\n" +
        "for i = 1, #KEYS do\n" +
        "    local stock = tonumber(redis.call('GET', KEYS[i]))\n" +
        "    if stock == nil or stock < tonumber(ARGV[i]) then\n" +
        "        return -1\n" +
        "    end\n" +
        "end\n" +
        "-- Step 2: All products have enough stock, decrease all\n" +
        "for i = 1, #KEYS do\n" +
        "    redis.call('DECRBY', KEYS[i], tonumber(ARGV[i]))\n" +
        "end\n" +
        "-- Step 3: Return success\n" +
        "return 1";

    public int getStock(String productId) {
        String stockStr = redisTemplate.opsForValue().get("stock:" + productId);
        return stockStr != null ? Integer.parseInt(stockStr) : 0;
    }

    /**
     * Atomically decreases stock for multiple products using Lua script.
     * @return true if success, false if out of stock
     */
    public boolean decreaseStock(List<StockItemRequest> items) {
        List<String> keys = items.stream()
            .map(item -> "stock:" + item.getProductId())
            .toList();

        Object[] args = items.stream()
            .map(item -> String.valueOf(item.getQuantity()))
            .toArray(Object[]::new);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(DECREASE_STOCK_LUA_SCRIPT);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(script, keys, args);
        
        if (result != null && result == 1) {
            log.info("✅ Stock decreased successfully for {} products", items.size());
            return true;
        } else {
            log.warn("❌ OUT_OF_STOCK: insufficient stock for one or more products");
            return false;
        }
    }
}
