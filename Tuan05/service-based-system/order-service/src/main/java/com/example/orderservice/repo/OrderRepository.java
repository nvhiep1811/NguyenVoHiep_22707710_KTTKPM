package com.example.orderservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orderservice.domain.OrderRecord;

public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
}
