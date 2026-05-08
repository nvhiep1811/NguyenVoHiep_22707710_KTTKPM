package com.kttkpm.notificationservice.repository;

import com.kttkpm.notificationservice.domain.EventLog;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventLogRepository extends MongoRepository<EventLog, String> {

    List<EventLog> findTop100ByOrderByTimestampDesc();

    List<EventLog> findTop100ByEventTypeOrderByTimestampDesc(String eventType);
}
