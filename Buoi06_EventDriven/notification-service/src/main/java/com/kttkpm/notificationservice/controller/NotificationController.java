package com.kttkpm.notificationservice.controller;

import com.kttkpm.notificationservice.domain.Notification;
import com.kttkpm.notificationservice.repository.NotificationRepository;
import com.kttkpm.notificationservice.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public NotificationController(NotificationRepository notificationRepository, NotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/{notificationId}")
    public Notification getById(@PathVariable String notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
    }

    @GetMapping("/users/{userId}")
    public List<Notification> getByUserId(@PathVariable String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/users/{userId}/unread")
    public List<Notification> getUnreadByUserId(@PathVariable String userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    @PatchMapping("/{notificationId}/read")
    public Notification markAsRead(@PathVariable String notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    @PatchMapping("/users/{userId}/read-all")
    public Map<String, Long> markAllAsRead(@PathVariable String userId) {
        long updated = notificationService.markAllAsRead(userId);
        return Map.of("updated", updated);
    }
}
