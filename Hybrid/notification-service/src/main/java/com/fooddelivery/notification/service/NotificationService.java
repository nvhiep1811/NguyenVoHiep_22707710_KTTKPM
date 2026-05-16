package com.fooddelivery.notification.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.fooddelivery.notification.event.PaymentFailedEvent;
import com.fooddelivery.notification.event.PaymentSuccessEvent;
import com.fooddelivery.notification.model.NotificationDocument;
import com.fooddelivery.notification.model.NotificationType;
import com.fooddelivery.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createPaymentSuccessNotification(PaymentSuccessEvent event) {
        String message = "Đơn hàng #" + event.orderId() + " đã thanh toán thành công!";
        save(event.userId(), event.orderId(), "Thanh toán thành công", message, NotificationType.PAYMENT_SUCCESS);
        System.out.println(message);
    }

    public void createPaymentFailedNotification(PaymentFailedEvent event) {
        String message = "Đơn hàng #" + event.orderId() + " thanh toán thất bại!";
        save(event.userId(), event.orderId(), "Thanh toán thất bại", message, NotificationType.PAYMENT_FAILED);
        System.out.println(message);
    }

    private void save(String userId, String orderId, String title, String message, NotificationType type) {
        NotificationDocument notification = new NotificationDocument();
        notification.setUserId(userId);
        notification.setOrderId(orderId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        notificationRepository.save(notification);
    }
}
