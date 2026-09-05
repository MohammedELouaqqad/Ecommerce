package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name ="orderItems")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;

     
    private Integer quantite;
    private Double price;
    private Double totalPrice;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnore
    private Order order;

    @ManyToOne
    private ProductEntity product;    

}
