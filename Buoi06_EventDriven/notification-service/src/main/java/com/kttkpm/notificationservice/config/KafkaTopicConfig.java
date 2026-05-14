package com.kttkpm.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic userRegisteredTopic(@Value("${app.kafka.topics.user-registered}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic userRegisteredDlq(@Value("${app.kafka.topics.user-registered}") String topic) {
        return TopicBuilder.name(topic + ".DLQ").partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic bookingCreatedTopic(@Value("${app.kafka.topics.booking-created}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic bookingCreatedDlq(@Value("${app.kafka.topics.booking-created}") String topic) {
        return TopicBuilder.name(topic + ".DLQ").partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic paymentCompletedTopic(@Value("${app.kafka.topics.payment-completed}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic paymentCompletedDlq(@Value("${app.kafka.topics.payment-completed}") String topic) {
        return TopicBuilder.name(topic + ".DLQ").partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic bookingFailedTopic(@Value("${app.kafka.topics.booking-failed}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic bookingFailedDlq(@Value("${app.kafka.topics.booking-failed}") String topic) {
        return TopicBuilder.name(topic + ".DLQ").partitions(1).replicas(1).build();
    }
}
