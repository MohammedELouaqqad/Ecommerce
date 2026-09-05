package com.example.demo.Service;


import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public ResponseEntity<?> CreateOrder( Order newOrder) {
        try{
            newOrder.setUser(userRepository.findById(newOrder.getUser().getId()).orElseThrow());

            newOrder.setOrderDate(LocalDate.now());

            for(OrderItem ordrItem : newOrder.getOrderItems()){

                ordrItem.setProduct(productRepository.findById(ordrItem.getProduct().getId()).orElseThrow());
                if(ordrItem.getQuantite() > ordrItem.getProduct().getCountStock()){
                    return ResponseEntity.notFound().build();
                }
            }

            orderRepository.save(newOrder);

            for(OrderItem ordrItem : newOrder.getOrderItems()){
                ordrItem.setOrder(newOrder);
                ordrItem.getProduct().setCountStock(ordrItem.getProduct().getCountStock() - ordrItem.getQuantite());
                orderItemRepository.save(ordrItem);

            }

            return ResponseEntity.ok(newOrder);

        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }

    public ResponseEntity<?> modifyOrder( Long id, Order order) {
        try{
            Optional<Order> editOrder = orderRepository.findById(id);
            editOrder.get().setStatus(order.getStatus());
            orderRepository.save(editOrder.get());

            return ResponseEntity.ok(editOrder.get());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server:"+e);
        }
    }
}
