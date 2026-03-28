package com.example.shippingservice.repo;

import com.example.shippingservice.domain.ShipmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<ShipmentRecord, Long> {
}
