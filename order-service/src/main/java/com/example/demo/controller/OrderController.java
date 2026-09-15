package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api")
public class OrderController {

    // Set by the api-gateway from the verified JWT (any client copy is stripped there).
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLES_HEADER = "X-User-Roles";


    @Autowired
    private OrderService orderService;

    // A customer gets only their own orders, an Admin gets all of them.
    @GetMapping("/customer/allOrders")
    public ResponseEntity<?> getAllOrders(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestHeader(value = USER_ROLES_HEADER, required = false) String roles) {
        if (userId == null) {
            return missingIdentity();
        }
        boolean isAdmin = roles != null && List.of(roles.split(",")).contains("ROLE_Admin");
        return ResponseEntity.ok(orderService.getOrders(userId, isAdmin));
    }

    // The order is always placed for the caller, whatever user the body claims.
    @PostMapping("/customer/addOrder")
    public ResponseEntity<?> CreateOrder(
            @RequestBody Order newOrder,
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestHeader(value = USER_EMAIL_HEADER, required = false) String userEmail) {
        if (userId == null || userEmail == null) {
            return missingIdentity();
        }
        return orderService.CreateOrder(newOrder, userId, userEmail);
    }

    // Reached without going through the api-gateway, or with a token issued before
    // the userId claim existed: the caller must log in again.
    private static ResponseEntity<String> missingIdentity() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing caller identity");
    }

    @PutMapping("/admin/editOrder/{id}")
    public ResponseEntity<?> modifyOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.modifyOrder(id, order);
    }

}
