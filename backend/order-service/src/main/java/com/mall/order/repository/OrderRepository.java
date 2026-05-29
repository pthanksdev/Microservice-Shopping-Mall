package com.mall.order.repository;

import com.mall.order.model.Order;
import com.mall.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
