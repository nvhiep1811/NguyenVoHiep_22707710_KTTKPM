package com.kttkpm.notificationservice.domain;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@CompoundIndex(name = "event_type_timestamp_idx", def = "{'eventType': 1, 'timestamp': -1}")
public class EventLog {

    @Id
    private String id;

    @Indexed(name = "event_type_idx")
    private String eventType;

    @Indexed(name = "source_idx")
    private String source;

    private Map<String, Object> payload;

    @Indexed(name = "status_idx")
    private EventStatus status;

    @CreatedDate
    @Indexed(name = "timestamp_idx")
    private Instant timestamp;

    public EventLog() {
    }

    public EventLog(String eventType, String source, Map<String, Object> payload, EventStatus status) {
        this.eventType = eventType;
        this.source = source;
        this.payload = payload;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
