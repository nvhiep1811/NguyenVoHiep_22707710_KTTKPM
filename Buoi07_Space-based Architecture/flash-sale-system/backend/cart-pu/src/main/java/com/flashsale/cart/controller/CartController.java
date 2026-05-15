package com.flashsale.cart.controller;

import com.flashsale.cart.dto.AddToCartRequest;
import com.flashsale.cart.dto.CartResponse;
import com.flashsale.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody AddToCartRequest request) {
        cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Added to cart successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Map<String, String>> removeFromCart(
            @PathVariable String userId,
            @PathVariable String productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Removed from cart successfully"));
    }
}
