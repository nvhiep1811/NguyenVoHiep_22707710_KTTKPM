package com.kttkpm.movieservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topics.booking-created}")
    private String bookingCreatedTopic;

    @Value("${app.kafka.topics.booking-failed}")
    private String bookingFailedTopic;

    @Bean
    public NewTopic bookingCreatedTopic() {
        return TopicBuilder.name(bookingCreatedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingCreatedDlq() {
        return TopicBuilder.name(bookingCreatedTopic + ".DLQ")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingFailedTopic() {
        return TopicBuilder.name(bookingFailedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingFailedDlq() {
        return TopicBuilder.name(bookingFailedTopic + ".DLQ")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
