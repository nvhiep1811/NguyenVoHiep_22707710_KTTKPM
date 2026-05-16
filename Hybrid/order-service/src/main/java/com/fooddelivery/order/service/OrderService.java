package com.fooddelivery.order.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fooddelivery.order.dto.CreateOrderItemRequest;
import com.fooddelivery.order.dto.CreateOrderRequest;
import com.fooddelivery.order.dto.CreateOrderResponse;
import com.fooddelivery.order.dto.OrderResponse;
import com.fooddelivery.order.event.OrderCreatedEvent;
import com.fooddelivery.order.event.OrderCreatedItem;
import com.fooddelivery.order.event.PaymentFailedEvent;
import com.fooddelivery.order.event.PaymentSuccessEvent;
import com.fooddelivery.order.exception.ApiException;
import com.fooddelivery.order.messaging.OrderEventPublisher;
import com.fooddelivery.order.model.OrderDocument;
import com.fooddelivery.order.model.OrderItem;
import com.fooddelivery.order.model.OrderStatus;
import com.fooddelivery.order.model.PaymentStatus;
import com.fooddelivery.order.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream().map(this::toOrderItem).toList();
        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Instant now = Instant.now();
        OrderDocument order = new OrderDocument();
        order.setUserId(request.userId().trim());
        order.setItems(items);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(request.paymentMethod());
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        OrderDocument saved = orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(toOrderCreatedEvent(saved));

        return new CreateOrderResponse(
                saved.getId(),
                saved.getStatus().name(),
                saved.getPaymentStatus().name(),
                saved.getTotalAmount(),
                "Order created. Payment is processing asynchronously."
        );
    }

    public List<OrderResponse> getOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrder(String id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public List<OrderResponse> getOrdersByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void markPaymentSuccess(PaymentSuccessEvent event) {
        orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.PAID);
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            log.info("Order {} updated to PAID/SUCCESS", event.orderId());
        }, () -> log.warn("Order {} not found for PAYMENT_SUCCESS event", event.orderId()));
    }

    public void markPaymentFailed(PaymentFailedEvent event) {
        orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            log.info("Order {} updated to PAYMENT_FAILED/FAILED", event.orderId());
        }, () -> log.warn("Order {} not found for PAYMENT_FAILED event", event.orderId()));
    }

    private OrderItem toOrderItem(CreateOrderItemRequest request) {
        BigDecimal lineTotal = request.price().multiply(BigDecimal.valueOf(request.quantity()));
        OrderItem item = new OrderItem();
        item.setFoodId(request.foodId());
        item.setFoodName(request.foodName());
        item.setPrice(request.price());
        item.setQuantity(request.quantity());
        item.setLineTotal(lineTotal);
        return item;
    }

    private OrderCreatedEvent toOrderCreatedEvent(OrderDocument order) {
        List<OrderCreatedItem> eventItems = order.getItems()
                .stream()
                .map(item -> new OrderCreatedItem(
                        item.getFoodId(),
                        item.getFoodName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "ORDER_CREATED",
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getPaymentMethod().name(),
                eventItems,
                order.getCreatedAt()
        );
    }

    private OrderResponse toResponse(OrderDocument order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getItems(),
                order.getTotalAmount(),
                order.getPaymentMethod().name(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}

