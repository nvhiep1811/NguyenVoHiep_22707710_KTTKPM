package com.kttkpm.paymentservice.controller;

import com.kttkpm.paymentservice.domain.Payment;
import com.kttkpm.paymentservice.dto.BookingCreatedEvent;
import com.kttkpm.paymentservice.repository.PaymentRepository;
import com.kttkpm.paymentservice.service.PaymentProcessorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorService paymentProcessorService;

    public PaymentController(PaymentRepository paymentRepository, PaymentProcessorService paymentProcessorService) {
        this.paymentRepository = paymentRepository;
        this.paymentProcessorService = paymentProcessorService;
    }

    @GetMapping("/{paymentId}")
    public Payment getById(@PathVariable String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @GetMapping("/booking/{bookingId}")
    public Payment getByBookingId(@PathVariable String bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @GetMapping("/users/{userId}")
    public List<Payment> getByUserId(@PathVariable String userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping("/simulate")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment simulateBookingCreated(@Valid @RequestBody BookingCreatedEvent event) {
        return paymentProcessorService.process(event);
    }
}
