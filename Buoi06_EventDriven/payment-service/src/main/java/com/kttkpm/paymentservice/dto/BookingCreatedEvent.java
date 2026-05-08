package com.kttkpm.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record BookingCreatedEvent(
        @NotBlank String bookingId,
        @NotBlank String userId,
        String movieId,
        @NotNull @Positive @JsonAlias("amount") BigDecimal totalPrice,
        String movieTitle) {
}
