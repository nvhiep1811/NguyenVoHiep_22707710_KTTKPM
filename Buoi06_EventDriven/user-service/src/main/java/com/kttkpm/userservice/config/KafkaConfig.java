package com.kttkpm.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userRegisteredDlqTopic() {
        return TopicBuilder.name(userRegisteredTopic + ".DLQ")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
