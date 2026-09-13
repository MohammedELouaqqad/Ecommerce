package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

// Kept in its own bean so that OrderService calls it through the Spring proxy:
// the transaction is committed (or rolled back) before control returns to the
// caller, which can then compensate the stock reservation on any failure,
// including one raised at commit time.
@Service
public class OrderPersistenceService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPersistenceService(OrderRepository orderRepository,
                                   OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // Order and its items are saved together or not at all.
    @Transactional
    public Order save(Order order) {
        orderRepository.save(order);
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);
        }
        return order;
    }
}
