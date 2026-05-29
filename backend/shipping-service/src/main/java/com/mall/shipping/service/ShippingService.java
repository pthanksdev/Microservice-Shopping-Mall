package com.mall.shipping.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.shipping.model.Shipment;
import com.mall.shipping.model.ShipmentStatus;
import com.mall.shipping.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order.confirmed", groupId = "shipping-group")
    @Transactional
    public void onOrderConfirmed(Map<String, Object> event) {
        String orderId = (String) event.get("orderId");
        log.info("Creating shipment for confirmed order: {}", orderId);

        Shipment shipment = Shipment.builder()
                .orderId(orderId)
                .status(ShipmentStatus.PENDING)
                .originAddress("Main Warehouse, Silicon Valley, CA")
                .destinationAddress("Injected from Order Service Address") // In real app, would be passed in event
                .build();

        shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment createLabel(String shipmentId, String carrier) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", shipmentId));

        shipment.setCarrier(carrier);
        shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        shipment.setStatus(ShipmentStatus.LABEL_CREATED);
        shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(3));

        Shipment saved = shipmentRepository.save(shipment);
        kafkaTemplate.send("shipment.label.created", Map.of("orderId", saved.getOrderId(), "trackingNumber", saved.getTrackingNumber()));
        return saved;
    }

    @Transactional
    public Shipment updateStatus(String shipmentId, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", shipmentId));

        shipment.setStatus(status);
        if (status == ShipmentStatus.DELIVERED) {
            shipment.setActualDelivery(LocalDateTime.now());
            kafkaTemplate.send("order.delivered", Map.of("orderId", shipment.getOrderId()));
        } else if (status == ShipmentStatus.IN_TRANSIT) {
            kafkaTemplate.send("order.shipped", Map.of("orderId", shipment.getOrderId(), "trackingNumber", shipment.getTrackingNumber()));
        }

        return shipmentRepository.save(shipment);
    }

    public Shipment getByOrderId(String orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "orderId", orderId));
    }
}
