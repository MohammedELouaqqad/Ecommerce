package com.example.demo.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId) {
        super("Not enough stock for product " + productId);
    }
}
