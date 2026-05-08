package com.kttkpm.paymentservice.dto;

import java.time.Instant;

public record DomainEvent<T>(
        String eventType,
        String source,
        T payload,
        String status,
        Instant timestamp) {

    public static <T> DomainEvent<T> published(String eventType, String source, T payload) {
        return new DomainEvent<>(eventType, source, payload, "PUBLISHED", Instant.now());
    }
}
