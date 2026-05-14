package com.kttkpm.notificationservice.service;

import com.kttkpm.notificationservice.domain.EventLog;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class EventStreamService {

    private static final Logger log = LoggerFactory.getLogger(EventStreamService.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe(List<EventLog> snapshot) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));

        snapshot.forEach(event -> send(emitter, "snapshot", event));
        send(emitter, "ready", "connected");

        return emitter;
    }

    public void publish(EventLog event) {
        emitters.forEach(emitter -> send(emitter, "event", event));
    }

    private void send(SseEmitter emitter, String name, Object data) {
        try {
            emitter.send(SseEmitter.event().name(name).data(data));
        } catch (IOException | IllegalStateException ex) {
            emitters.remove(emitter);
            log.debug("Removed closed SSE emitter: {}", ex.toString());
        }
    }
}
