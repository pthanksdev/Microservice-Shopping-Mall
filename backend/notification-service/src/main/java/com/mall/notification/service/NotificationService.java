package com.mall.notification.service;

import com.mall.notification.model.Notification;
import com.mall.notification.model.NotificationType;
import com.mall.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @KafkaListener(topics = "order.placed", groupId = "notification-group")
    public void onOrderPlaced(Map<String, Object> event) {
        log.info("Sending ORDER_PLACED notification for order: {}", event.get("orderId"));
        sendEmailNotification(
                (String) event.get("email"),
                "Order Confirmed! 🛍️",
                "Your order #" + event.get("orderId") + " has been placed successfully.",
                (String) event.get("customerId"),
                NotificationType.ORDER_PLACED,
                (String) event.get("orderId")
        );
    }

    @KafkaListener(topics = "payment.succeeded", groupId = "notification-group")
    public void onPaymentSucceeded(Map<String, Object> event) {
        log.info("Sending PAYMENT_SUCCEEDED notification for order: {}", event.get("orderId"));
        sendEmailNotification(
                (String) event.get("email"),
                "Payment Received ✅",
                "Your payment for order #" + event.get("orderId") + " was successful!",
                (String) event.get("customerId"),
                NotificationType.PAYMENT_SUCCEEDED,
                (String) event.get("orderId")
        );
    }

    @KafkaListener(topics = "vendor.approved", groupId = "notification-group")
    public void onVendorApproved(Map<String, Object> event) {
        log.info("Sending VENDOR_APPROVED notification for vendor: {}", event.get("vendorId"));
        sendEmailNotification(
                (String) event.get("email"),
                "Your shop has been approved! 🎉",
                "Congratulations! Your vendor account is now active. Start listing your products.",
                (String) event.get("userId"),
                NotificationType.VENDOR_APPROVED,
                (String) event.get("vendorId")
        );
    }

    private void sendEmailNotification(String email, String subject, String body,
                                        String recipientId, NotificationType type, String referenceId) {
        // Save in-app notification
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .recipientEmail(email != null ? email : "")
                .type(type)
                .title(subject)
                .body(body)
                .referenceId(referenceId)
                .build();

        // Send email if we have the recipient's email
        if (email != null && !email.isBlank()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                notification.setSent(true);
                log.info("Email sent to: {}", email);
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", email, e.getMessage());
            }
        }

        notificationRepository.save(notification);
    }
}
