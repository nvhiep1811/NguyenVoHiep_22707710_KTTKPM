package com.kttkpm.paymentservice.repository;

import com.kttkpm.paymentservice.domain.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByBookingId(String bookingId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);
}
