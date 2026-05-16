package com.fooddelivery.payment.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fooddelivery.payment.model.PaymentDocument;

public interface PaymentRepository extends MongoRepository<PaymentDocument, String> {

    Optional<PaymentDocument> findByOrderId(String orderId);
}

