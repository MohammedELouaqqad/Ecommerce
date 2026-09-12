package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Order;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order,Long>{
    Optional<Order> findById(Long id);
    
}