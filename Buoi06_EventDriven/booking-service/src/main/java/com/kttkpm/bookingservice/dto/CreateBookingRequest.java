package com.kttkpm.bookingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateBookingRequest(
        @NotBlank String userId,
        @NotBlank String movieId,
        @NotBlank String showTimeId,
        @NotBlank String movieTitle,
        @NotNull @Positive Integer seats,
        @NotNull @Positive BigDecimal totalPrice) {
}
