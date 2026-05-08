package com.kttkpm.notificationservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kttkpm.notificationservice.domain.EventLog;
import com.kttkpm.notificationservice.domain.EventStatus;
import com.kttkpm.notificationservice.domain.Notification;
import com.kttkpm.notificationservice.domain.NotificationType;
import com.kttkpm.notificationservice.dto.BookingFailedEvent;
import com.kttkpm.notificationservice.dto.PaymentCompletedEvent;
import com.kttkpm.notificationservice.repository.EventLogRepository;
import com.kttkpm.notificationservice.repository.NotificationRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    public NotificationService(
            NotificationRepository notificationRepository,
            EventLogRepository eventLogRepository,
            ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.eventLogRepository = eventLogRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentCompleted(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            EventContext eventContext = readEventContext(payload, topic);
            PaymentCompletedEvent event = objectMapper.treeToValue(eventContext.payload(), PaymentCompletedEvent.class);
            saveEvent(eventContext);
            Notification notification = new Notification(
                    event.userId(),
                    event.bookingId(),
                    NotificationType.BOOKING_SUCCESS,
                    "Booking " + event.bookingId() + " was paid successfully. Amount: " + event.amount());
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Created success notification id={} for userId={}", savedNotification.getId(), event.userId());
        } catch (Exception ex) {
            log.error("Cannot consume PAYMENT_COMPLETED payload: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingFailed(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            EventContext eventContext = readEventContext(payload, topic);
            BookingFailedEvent event = objectMapper.treeToValue(eventContext.payload(), BookingFailedEvent.class);
            saveEvent(eventContext);
            Notification notification = new Notification(
                    event.userId(),
                    event.bookingId(),
                    NotificationType.BOOKING_FAILED,
                    "Booking " + event.bookingId() + " failed: " + event.reason());
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Created failed notification id={} for userId={}", savedNotification.getId(), event.userId());
        } catch (Exception ex) {
            log.error("Cannot consume BOOKING_FAILED payload: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingCreatedForAudit(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            EventContext eventContext = readEventContext(payload, topic);
            saveEvent(eventContext);
            log.info("Stored audit event type={} source={}", eventContext.eventType(), eventContext.source());
        } catch (Exception ex) {
            log.error("Cannot consume BOOKING_CREATED payload for audit: {}", payload, ex);
        }
    }

    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public long markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    private EventContext readEventContext(String payload, String topic) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        JsonNode eventPayload = root.hasNonNull("payload") ? root.get("payload") : root;
        String eventType = root.hasNonNull("eventType") ? root.get("eventType").asText() : topic;
        String source = root.hasNonNull("source") ? root.get("source").asText() : inferSource(topic);
        return new EventContext(eventType, source, eventPayload);
    }

    private String inferSource(String topic) {
        if ("BOOKING_CREATED".equals(topic)) {
            return "booking-service";
        }
        return "payment-service";
    }

    private void saveEvent(EventContext eventContext) {
        Map<String, Object> payloadMap = objectMapper.convertValue(eventContext.payload(), new TypeReference<>() {
        });
        eventLogRepository.save(new EventLog(
                eventContext.eventType(),
                eventContext.source(),
                payloadMap,
                EventStatus.CONSUMED));
    }

    private record EventContext(String eventType, String source, JsonNode payload) {
    }
}
