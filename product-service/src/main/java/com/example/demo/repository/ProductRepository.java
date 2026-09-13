package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    Optional<ProductEntity> findByName(String name);
    boolean existsByName(String name);

    // Check and decrement in a single statement: the row lock taken by the UPDATE
    // makes the stock condition race-free. Returns 0 when the stock is insufficient.
    @Modifying
    @Query("UPDATE ProductEntity p SET p.countStock = p.countStock - :qty WHERE p.id = :id AND p.countStock >= :qty")
    int decrementIfAvailable(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.countStock = p.countStock + :qty WHERE p.id = :id")
    int increment(@Param("id") Long id, @Param("qty") int qty);
}
