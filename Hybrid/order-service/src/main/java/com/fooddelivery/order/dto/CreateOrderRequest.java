package com.fooddelivery.order.dto;

import java.util.List;

import com.fooddelivery.order.model.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotBlank String userId,
        @NotEmpty(message = "Order items must not be empty") List<@Valid CreateOrderItemRequest> items,
        @NotNull PaymentMethod paymentMethod
) {
}

