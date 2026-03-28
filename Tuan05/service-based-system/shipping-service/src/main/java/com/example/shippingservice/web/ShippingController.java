package com.example.shippingservice.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shippingservice.domain.ShipmentRecord;
import com.example.shippingservice.repo.ShipmentRepository;

@RestController
@RequestMapping("/api/shipments")
public class ShippingController {

    private final ShipmentRepository shipmentRepository;

    public ShippingController(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @GetMapping
    public List<ShipmentRecord> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "shipping-service", "status", "UP");
    }
}
