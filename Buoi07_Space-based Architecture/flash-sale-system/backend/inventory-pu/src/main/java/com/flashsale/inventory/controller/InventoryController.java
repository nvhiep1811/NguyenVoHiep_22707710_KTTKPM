package com.flashsale.inventory.controller;

import com.flashsale.inventory.dto.*;
import com.flashsale.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock/{productId}")
    public ResponseEntity<StockResponse> getStock(@PathVariable String productId) {
        int stock = inventoryService.getStock(productId);
        return ResponseEntity.ok(new StockResponse(productId, stock));
    }

    @PostMapping("/stock/decrease")
    public ResponseEntity<DecreaseStockResponse> decreaseStock(@RequestBody DecreaseStockRequest request) {
        boolean success = inventoryService.decreaseStock(request.getItems());
        if (success) {
            return ResponseEntity.ok(new DecreaseStockResponse("SUCCESS", "Stock decreased successfully"));
        } else {
            return ResponseEntity.badRequest()
                .body(new DecreaseStockResponse("FAILED", "OUT_OF_STOCK"));
        }
    }
}
