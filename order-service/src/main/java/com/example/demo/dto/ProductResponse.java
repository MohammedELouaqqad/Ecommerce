package com.example.demo.dto;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private Double price;
    private Integer countStock;
}