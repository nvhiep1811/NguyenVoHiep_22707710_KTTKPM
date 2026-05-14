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
    private final EventStreamService eventStreamService;
    private final ObjectMapper objectMapper;

    public NotificationService(
            NotificationRepository notificationRepository,
            EventLogRepository eventLogRepository,
            EventStreamService eventStreamService,
            ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.eventLogRepository = eventLogRepository;
        this.eventStreamService = eventStreamService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentCompleted(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws Exception {
        EventContext eventContext = readEventContext(payload, topic);
        PaymentCompletedEvent event = objectMapper.treeToValue(eventContext.payload(), PaymentCompletedEvent.class);
        Notification notification = new Notification(
                event.userId(),
                event.bookingId(),
                NotificationType.BOOKING_SUCCESS,
                "Booking " + event.bookingId() + " was paid successfully. Amount: " + event.amount());
        Notification savedNotification = notificationRepository.save(notification);
        saveEvent(eventContext, EventStatus.CONSUMED);
        log.info("Created success notification id={} for userId={}", savedNotification.getId(), event.userId());
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingFailed(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws Exception {
        EventContext eventContext = readEventContext(payload, topic);
        BookingFailedEvent event = objectMapper.treeToValue(eventContext.payload(), BookingFailedEvent.class);
        Notification notification = new Notification(
                event.userId(),
                event.bookingId(),
                NotificationType.BOOKING_FAILED,
                "Booking " + event.bookingId() + " failed: " + event.reason());
        Notification savedNotification = notificationRepository.save(notification);
        saveEvent(eventContext, EventStatus.CONSUMED);
        log.info("Created failed notification id={} for userId={}", savedNotification.getId(), event.userId());
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingCreatedForAudit(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws Exception {
        EventContext eventContext = readEventContext(payload, topic);
        saveEvent(eventContext, EventStatus.CONSUMED);
        log.info("Stored audit event type={} source={}", eventContext.eventType(), eventContext.source());
    }

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegisteredForAudit(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws Exception {
        EventContext eventContext = readEventContext(payload, topic);
        saveEvent(eventContext, EventStatus.CONSUMED);
        log.info("Stored audit event type={} source={}", eventContext.eventType(), eventContext.source());
    }

    @KafkaListener(
            topics = {
                    "${app.kafka.topics.user-registered}.DLQ",
                    "${app.kafka.topics.booking-created}.DLQ",
                    "${app.kafka.topics.payment-completed}.DLQ",
                    "${app.kafka.topics.booking-failed}.DLQ"
            },
            groupId = "${spring.kafka.consumer.group-id}-dlq")
    public void consumeDeadLetter(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            EventContext eventContext = readEventContext(payload, topic);
            saveEvent(eventContext, EventStatus.DEAD_LETTER);
            log.warn("Stored DLQ event type={} topic={}", eventContext.eventType(), topic);
        } catch (Exception ex) {
            saveRawDeadLetter(topic, payload, ex);
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
        if (root.isTextual()) {
            root = objectMapper.readTree(root.asText());
        }
        JsonNode eventPayload = root.hasNonNull("payload") ? root.get("payload") : root;
        String cleanTopic = topic.endsWith(".DLQ") ? topic.substring(0, topic.length() - 4) : topic;
        String eventType = root.hasNonNull("eventType") ? root.get("eventType").asText() : cleanTopic;
        String source = root.hasNonNull("source") ? root.get("source").asText() : inferSource(cleanTopic);
        return new EventContext(eventType, source, eventPayload);
    }

    private String inferSource(String topic) {
        if ("USER_REGISTERED".equals(topic)) {
            return "user-service";
        }
        if ("BOOKING_CREATED".equals(topic)) {
            return "booking-service";
        }
        return "payment-service";
    }

    private EventLog saveEvent(EventContext eventContext, EventStatus status) {
        Map<String, Object> payloadMap = objectMapper.convertValue(eventContext.payload(), new TypeReference<>() {
        });
        EventLog eventLog = eventLogRepository.save(new EventLog(
                eventContext.eventType(),
                eventContext.source(),
                payloadMap,
                status));
        eventStreamService.publish(eventLog);
        return eventLog;
    }

    private void saveRawDeadLetter(String topic, String payload, Exception ex) {
        EventLog eventLog = eventLogRepository.save(new EventLog(
                topic.endsWith(".DLQ") ? topic.substring(0, topic.length() - 4) : topic,
                inferSource(topic.endsWith(".DLQ") ? topic.substring(0, topic.length() - 4) : topic),
                Map.of("rawPayload", payload, "error", String.valueOf(ex.getMessage())),
                EventStatus.DEAD_LETTER));
        eventStreamService.publish(eventLog);
        log.error("Stored raw DLQ payload from topic={}", topic, ex);
    }

    private record EventContext(String eventType, String source, JsonNode payload) {
    }
}
