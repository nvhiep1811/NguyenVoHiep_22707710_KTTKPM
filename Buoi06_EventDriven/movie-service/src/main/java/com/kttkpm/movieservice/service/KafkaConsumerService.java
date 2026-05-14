package com.kttkpm.movieservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final MovieService movieService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(MovieService movieService, ObjectMapper objectMapper) {
        this.movieService = movieService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingCreated(String message) throws Exception {
        JsonNode payload = readRequiredPayload(message);
        String movieId = payload.get("movieId").asText();
        String showTimeId = payload.get("showTimeId").asText();
        int seats = payload.get("seats").asInt();

        log.info("Received BOOKING_CREATED event. Decreasing available seats by {}", seats);
        movieService.updateAvailableSeats(movieId, showTimeId, -seats);
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingFailed(String message) throws Exception {
        JsonNode payload = readRequiredPayload(message);
        String movieId = payload.get("movieId").asText();
        String showTimeId = payload.get("showTimeId").asText();
        int seats = payload.get("seats").asInt();

        log.info("Received BOOKING_FAILED event. Restoring available seats by {}", seats);
        movieService.updateAvailableSeats(movieId, showTimeId, seats);
    }

    private JsonNode readRequiredPayload(String message) throws Exception {
        JsonNode root = objectMapper.readTree(message);
        JsonNode payload = root.has("payload") ? root.get("payload") : root;

        if (!payload.hasNonNull("movieId") || !payload.hasNonNull("showTimeId") || !payload.hasNonNull("seats")) {
            throw new IllegalArgumentException("Event payload must include movieId, showTimeId and seats");
        }
        return payload;
    }
}
