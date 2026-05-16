package com.fooddelivery.order.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fooddelivery.order.model.OrderDocument;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    List<OrderDocument> findByUserIdOrderByCreatedAtDesc(String userId);
}

