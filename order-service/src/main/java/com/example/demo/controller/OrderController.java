package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.demo.service.OrderService;
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
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class OrderController {


    @Autowired
    private OrderService orderService;

     @GetMapping("/customer/allOrders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/customer/addOrder")
    public ResponseEntity<?> CreateOrder(@RequestBody Order newOrder) {
        return orderService.CreateOrder(newOrder);
    }    

    @PutMapping("/admin/editOrder/{id}")
    public ResponseEntity<?> modifyOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.modifyOrder(id, order);
    }

}
