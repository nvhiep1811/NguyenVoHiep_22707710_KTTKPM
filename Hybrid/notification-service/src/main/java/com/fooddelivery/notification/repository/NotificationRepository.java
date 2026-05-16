package com.fooddelivery.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fooddelivery.notification.model.NotificationDocument;

public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {
}

