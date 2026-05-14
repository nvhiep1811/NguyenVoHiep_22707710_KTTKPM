package com.kttkpm.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ResilientKafkaPublisher {
    private static final Logger log = LoggerFactory.getLogger(ResilientKafkaPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ResilientKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishWithRetry(String topic, String key, Object payload) {
        int attempts = 0;
        int maxAttempts = 3;
        long backoffMs = 200L;

        while (attempts < maxAttempts) {
            attempts++;
            try {
                kafkaTemplate.send(topic, key, payload).get(Duration.ofSeconds(5).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
                log.info("Published event to {} (attempt {})", topic, attempts);
                return;
            } catch (Exception e) {
                log.warn("Failed to publish to {} (attempt {}): {}", topic, attempts, e.toString());
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                backoffMs *= 2;
            }
        }

        // Last resort: publish to DLQ topic (topic + ".DLQ") asynchronously
        String dlq = topic + ".DLQ";
        try {
            kafkaTemplate.send(dlq, key, payload);
            log.warn("Published event to DLQ {} after {} attempts", dlq, attempts);
        } catch (Exception ex) {
            log.error("Failed to publish to DLQ {}: {}", dlq, ex.toString());
        }
    }
}
