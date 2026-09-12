package com.example.demo.controller;

import com.example.demo.Service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.ProductEntity;
import com.example.demo.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PutMapping("/customer/decreaseStock/{id}/{quantity}")
    public ResponseEntity<?> decreaseStock(
        @PathVariable Long id,
        @PathVariable Integer quantity) {
            return productService.decreaseStock(id, quantity);
    }
     
}
