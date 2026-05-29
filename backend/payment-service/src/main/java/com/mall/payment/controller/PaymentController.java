package com.mall.payment.controller;

import com.mall.common.response.ApiResponse;
import com.mall.payment.model.Payment;
import com.mall.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Stripe payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/intent")
    @Operation(summary = "Create a Stripe PaymentIntent for an order")
    public ResponseEntity<ApiResponse<Payment>> createIntent(
            @RequestHeader("X-User-Id") String customerId,
            @RequestParam String orderId,
            @RequestParam BigDecimal amount) throws Exception {
        Payment payment = paymentService.createPaymentIntent(orderId, customerId, amount);
        return ResponseEntity.ok(ApiResponse.success("PaymentIntent created", payment));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public ResponseEntity<ApiResponse<Payment>> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByOrderId(orderId)));
    }

    @PostMapping("/webhooks/stripe")
    @Operation(summary = "Stripe webhook endpoint")
    public ResponseEntity<String> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        // Simplified — production should verify signature with Stripe.Webhook.constructEvent()
        // Parse event type and paymentIntentId from payload here
        return ResponseEntity.ok("received");
    }
}
