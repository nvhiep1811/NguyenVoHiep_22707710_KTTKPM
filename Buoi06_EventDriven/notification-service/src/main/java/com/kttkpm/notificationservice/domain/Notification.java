package com.kttkpm.notificationservice.domain;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "notifications")
@CompoundIndex(name = "user_read_created_at_idx", def = "{'userId': 1, 'isRead': 1, 'createdAt': -1}")
public class Notification {

    @Id
    private String id;

    @Indexed(name = "user_id_idx")
    private String userId;

    @Indexed(name = "booking_id_idx")
    private String bookingId;

    private NotificationType type;

    private String message;

    @Indexed(name = "is_read_idx")
    @Field("isRead")
    private boolean read;

    @CreatedDate
    private Instant createdAt;

    public Notification() {
    }

    public Notification(String userId, String bookingId, NotificationType type, String message) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.type = type;
        this.message = message;
        this.read = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
