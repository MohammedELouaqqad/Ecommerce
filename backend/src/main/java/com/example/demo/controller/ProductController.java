package com.example.demo.controller;

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
    private ProductRepository productRepository;

    @GetMapping("/customer/allProducts")
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }


    @PostMapping("/admin/addProduct")
    public ResponseEntity<?> CreateProduct(@RequestBody ProductEntity newProduct) {
        try{
            if(productRepository.existsByName(newProduct.getName())){
                return ResponseEntity.badRequest().body(null);
            }

            productRepository.save(newProduct);
            return ResponseEntity.ok(newProduct);

        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }

    @DeleteMapping("/admin/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        try{
            Optional <ProductEntity> product = productRepository.findById(id);
            if(product.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted with Success");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }

    @PutMapping("/admin/editProduct/{id}")
    public ResponseEntity<?> modifyProduct(@PathVariable Long id, @RequestBody ProductEntity product) {
        try{
            Optional <ProductEntity> editProduct = productRepository.findById(id);
            if(editProduct.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            editProduct.get().setName(product.getName());
            editProduct.get().setCountStock(product.getCountStock());
            editProduct.get().setDescription(product.getDescription());
            editProduct.get().setPrice(product.getPrice());
            editProduct.get().setSizes(product.getSizes());
            editProduct.get().setSku(product.getSku());
            editProduct.get().setFilename(product.getFilename());

            productRepository.save(editProduct.get());
            return ResponseEntity.ok("Product updated with Success");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the server");
        }
    }
     
}
