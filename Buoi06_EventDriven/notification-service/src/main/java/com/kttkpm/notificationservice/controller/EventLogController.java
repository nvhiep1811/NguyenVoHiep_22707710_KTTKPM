package com.kttkpm.notificationservice.controller;

import com.kttkpm.notificationservice.domain.EventLog;
import com.kttkpm.notificationservice.repository.EventLogRepository;
import com.kttkpm.notificationservice.service.EventStreamService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/events")
public class EventLogController {

    private final EventLogRepository eventLogRepository;
    private final EventStreamService eventStreamService;

    public EventLogController(EventLogRepository eventLogRepository, EventStreamService eventStreamService) {
        this.eventLogRepository = eventLogRepository;
        this.eventStreamService = eventStreamService;
    }

    @GetMapping
    public List<EventLog> getLatestEvents() {
        return eventLogRepository.findTop100ByOrderByTimestampDesc();
    }

    @GetMapping("/types/{eventType}")
    public List<EventLog> getLatestEventsByType(@PathVariable String eventType) {
        return eventLogRepository.findTop100ByEventTypeOrderByTimestampDesc(eventType);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        return eventStreamService.subscribe(eventLogRepository.findTop100ByOrderByTimestampDesc());
    }
}
