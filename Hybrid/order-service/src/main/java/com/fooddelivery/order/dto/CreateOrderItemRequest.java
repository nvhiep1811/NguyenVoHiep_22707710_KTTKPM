package com.fooddelivery.order.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(
        @NotBlank String foodId,
        @NotBlank String foodName,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @Min(value = 1, message = "Quantity must be greater than 0") int quantity
) {
}

