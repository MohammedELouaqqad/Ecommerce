package com.example.demo.Service;


import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ProductResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestClient;



@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private final RestClient restClient;

    public OrderService(RestClient restClient) {
        this.restClient = restClient;
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public ResponseEntity<?> CreateOrder(Order newOrder) {

        try {
            newOrder.setOrderDate(LocalDate.now());
            // 1. Check every product and its stock
            for (OrderItem orderItem : newOrder.getOrderItems()) {

                ProductResponse product = restClient.get()
                        .uri("http://product-service:8082/api/customer/product/"
                                + orderItem.getProductId())
                        .retrieve()
                        .body(ProductResponse.class);
                if (product == null) {
                    return ResponseEntity.notFound().build();
                }
                if (orderItem.getQuantite() > product.getCountStock()) {
                    return ResponseEntity.badRequest()
                            .body("Not enough stock for product "
                                    + orderItem.getProductId());
                }
                // Price at the moment of purchase
                orderItem.setPrice(product.getPrice());

                // Total for this item
                orderItem.setTotalPrice(
                        product.getPrice() * orderItem.getQuantite()
                );
            }

            // 2. Calculate total order price
            double total = 0;

            for (OrderItem orderItem : newOrder.getOrderItems()) {
                total += orderItem.getTotalPrice();
            }

            newOrder.setTotalprice(total);

            // 3. Save Order first
            orderRepository.save(newOrder);

            // 4. Save OrderItems + decrease product stock
            for (OrderItem orderItem : newOrder.getOrderItems()) {

                orderItem.setOrder(newOrder);

                // Decrease stock in Product Service
                restClient.put()
                        .uri("http://product-service:8082/api/customer/decreaseStock/"
                                + orderItem.getProductId()
                                + "/"
                                + orderItem.getQuantite())
                        .retrieve()
                        .toBodilessEntity();

                // Save OrderItem
                orderItemRepository.save(orderItem);
            }

            return ResponseEntity.ok(newOrder);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("Error in the Server");
        }
    }

    public ResponseEntity<?> modifyOrder(Long id, Order order) {
        try {
            Optional<Order> existingOrder = orderRepository.findById(id);

            if (existingOrder.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Order editOrder = existingOrder.get();

            editOrder.setStatus(order.getStatus());

            orderRepository.save(editOrder);

            return ResponseEntity.ok(editOrder);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error in the Server: " + e.getMessage());
        }
    }
}
