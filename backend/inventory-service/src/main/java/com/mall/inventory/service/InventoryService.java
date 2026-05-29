package com.mall.inventory.service;

import com.mall.common.exception.BusinessException;
import com.mall.common.exception.ResourceNotFoundException;
import com.mall.inventory.model.InventoryItem;
import com.mall.inventory.model.StockStatus;
import com.mall.inventory.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryService(InventoryRepository inventoryRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order.placed", groupId = "inventory-group")
    @Transactional
    public void onOrderPlaced(Map<String, Object> event) {
        log.info("Inventory reservation: {}", event.get("orderId"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                String productId = (String) item.get("productId");
                Object q = item.get("quantity");
                int quantity = q instanceof Integer ? (Integer) q : Integer.parseInt(q.toString());
                reserveStock(productId, quantity);
            }
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-group")
    @Transactional
    public void onOrderCancelled(Map<String, Object> event) {
        log.info("Inventory release: {}", event.get("orderId"));
    }

    @KafkaListener(topics = "order.confirmed", groupId = "inventory-group")
    @Transactional
    public void handleOrderConfirmation(Map<String, Object> event) {
        log.info("Inventory deduction: {}", event.get("orderId"));
    }

    public InventoryItem getByProductId(String productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", "productId", productId));
    }

    @Transactional
    public InventoryItem createOrUpdate(String productId, String variantId, String sku,
                                        int quantity, String warehouseLocation, int lowStockThreshold) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
                .orElse(InventoryItem.builder().productId(productId).build());

        item.setVariantId(variantId);
        item.setSku(sku);
        item.setQuantityAvailable(quantity);
        item.setWarehouseLocation(warehouseLocation);
        item.setLowStockThreshold(lowStockThreshold);
        if (quantity <= 0) {
            item.setStatus(StockStatus.OUT_OF_STOCK);
        } else if (quantity <= lowStockThreshold) {
            item.setStatus(StockStatus.LOW_STOCK);
        } else {
            item.setStatus(StockStatus.IN_STOCK);
        }

        return inventoryRepository.save(item);
    }

    @Transactional
    public void reserveStock(String productId, int quantity) {
        InventoryItem item = getByProductId(productId);
        if (item.getQuantityOnHand() < quantity) {
            throw new BusinessException("Insufficient stock: " + productId);
        }
        item.setQuantityReserved(item.getQuantityReserved() + quantity);
        if (item.getQuantityOnHand() <= 0) {
            item.setStatus(StockStatus.OUT_OF_STOCK);
            kafkaTemplate.send("stock.depleted", Map.of("productId", productId));
        } else if (item.getLowStockThreshold() != null && item.getQuantityOnHand() <= item.getLowStockThreshold()) {
            item.setStatus(StockStatus.LOW_STOCK);
        }
        inventoryRepository.save(item);
        kafkaTemplate.send("stock.reserved", Map.of("productId", productId, "quantity", quantity));
    }

    @Transactional
    public void releaseReservation(String productId, int quantity) {
        InventoryItem item = getByProductId(productId);
        item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - quantity));
        if (item.getQuantityOnHand() > 0) {
            if (item.getLowStockThreshold() != null && item.getQuantityOnHand() <= item.getLowStockThreshold()) {
                item.setStatus(StockStatus.LOW_STOCK);
            } else {
                item.setStatus(StockStatus.IN_STOCK);
            }
        }
        inventoryRepository.save(item);
        kafkaTemplate.send("stock.released", Map.of("productId", productId, "quantity", quantity));
    }

    @Transactional
    public void confirmDeduction(String productId, int quantity) {
        InventoryItem item = getByProductId(productId);
        item.setQuantityAvailable(item.getQuantityAvailable() - quantity);
        item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - quantity));
        inventoryRepository.save(item);
    }
}
