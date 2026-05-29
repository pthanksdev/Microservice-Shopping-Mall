package com.mall.payment.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.payment.model.Payment;
import com.mall.payment.model.PaymentStatus;
import com.mall.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public Payment createPaymentIntent(String orderId, String customerId, BigDecimal amount) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue()) // cents
                .setCurrency("usd")
                .putMetadata("orderId", orderId)
                .putMetadata("customerId", customerId)
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        Payment payment = Payment.builder()
                .orderId(orderId)
                .customerId(customerId)
                .stripePaymentIntentId(intent.getId())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment handleStripeWebhook(String paymentIntentId, String event) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentIntentId", paymentIntentId));

        switch (event) {
            case "payment_intent.succeeded" -> {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                kafkaTemplate.send("payment.succeeded", Map.of(
                        "orderId", payment.getOrderId(),
                        "paymentId", payment.getId(),
                        "customerId", payment.getCustomerId()
                ));
                log.info("Payment succeeded for order: {}", payment.getOrderId());
            }
            case "payment_intent.payment_failed" -> {
                payment.setStatus(PaymentStatus.FAILED);
                kafkaTemplate.send("payment.failed", Map.of(
                        "orderId", payment.getOrderId(),
                        "customerId", payment.getCustomerId()
                ));
                log.warn("Payment failed for order: {}", payment.getOrderId());
            }
            default -> log.info("Unhandled Stripe event: {}", event);
        }

        return paymentRepository.save(payment);
    }

    public Payment getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
    }
}
