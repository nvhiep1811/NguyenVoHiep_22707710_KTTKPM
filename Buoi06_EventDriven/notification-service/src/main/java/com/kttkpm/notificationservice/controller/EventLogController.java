package com.kttkpm.notificationservice.controller;

import com.kttkpm.notificationservice.domain.EventLog;
import com.kttkpm.notificationservice.repository.EventLogRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventLogController {

    private final EventLogRepository eventLogRepository;

    public EventLogController(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    @GetMapping
    public List<EventLog> getLatestEvents() {
        return eventLogRepository.findTop100ByOrderByTimestampDesc();
    }

    @GetMapping("/types/{eventType}")
    public List<EventLog> getLatestEventsByType(@PathVariable String eventType) {
        return eventLogRepository.findTop100ByEventTypeOrderByTimestampDesc(eventType);
    }
}
