package com.example.demo.controller;

import com.example.demo.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.StockLine;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.models.ProductEntity;
import com.example.demo.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@CrossOrigin
@RestController
@RequestMapping("/api")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @GetMapping("/customer/allProducts")
    public List<ProductEntity> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/customer/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping("/admin/addProduct")
    public ResponseEntity<?> CreateProduct(@RequestBody ProductEntity newProduct) {
        return productService.CreateProduct(newProduct);
    }

    @DeleteMapping("/admin/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        return productService.deleteProduct(id);
    }

    @PutMapping("/admin/editProduct/{id}")
    public ResponseEntity<?> modifyProduct(
        @PathVariable Long id,
        @RequestBody ProductEntity product) {
        
            return productService.modifyProduct(id, product);
    }

    // Internal endpoints, called by order-service only: not routed by the api-gateway.
    @PostMapping("/internal/stock/reserve")
    public ResponseEntity<?> reserveStock(@RequestBody List<StockLine> lines) {
        productService.reserveStock(lines);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/stock/release")
    public ResponseEntity<?> releaseStock(@RequestBody List<StockLine> lines) {
        productService.releaseStock(lines);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
