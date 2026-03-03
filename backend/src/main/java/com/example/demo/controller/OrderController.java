package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.models.ProductEntity;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class OrderController {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

     @GetMapping("/customer/allOrders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/customer/addOrder")
    public ResponseEntity<?> CreateOrder(@RequestBody Order newOrder) {
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

    @PutMapping("/admin/editOrder/{id}")
    public ResponseEntity<?> putMethodName(@PathVariable Long id, @RequestBody Order order) {
        try{
            Optional<Order> editOrder = orderRepository.findById(id);
            editOrder.get().setStatus(order.getStatus());
            orderRepository.save(editOrder.get());

            return ResponseEntity.ok(editOrder.get());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }

}
