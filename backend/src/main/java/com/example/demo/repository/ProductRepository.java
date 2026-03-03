package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    Optional<ProductEntity> findByName(String name);
    boolean existsByName(String name);
}
