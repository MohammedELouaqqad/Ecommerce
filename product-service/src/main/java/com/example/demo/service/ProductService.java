package com.example.demo.service;

import com.example.demo.dto.StockLine;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.models.ProductEntity;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }
    

    public ResponseEntity<?> getProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }



    public ResponseEntity<?> CreateProduct( ProductEntity newProduct) {
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

    public ResponseEntity<?> deleteProduct( Long id){
        try{
            Optional<ProductEntity> product = productRepository.findById(id);
            if(product.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted with Success");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }



    public ResponseEntity<?> modifyProduct( Long id, ProductEntity product) {
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

 
    // All-or-nothing: an insufficient line throws, which rolls back every
    // decrement already made for this order.
    @Transactional
    public void reserveStock(List<StockLine> lines) {
        for (StockLine line : sortedByProduct(lines)) {
            if (productRepository.decrementIfAvailable(line.productId(), line.quantity()) == 0) {
                throw new InsufficientStockException(line.productId());
            }
        }
    }

    // Compensation for reserveStock, used when the order could not be saved.
    @Transactional
    public void releaseStock(List<StockLine> lines) {
        for (StockLine line : sortedByProduct(lines)) {
            productRepository.increment(line.productId(), line.quantity());
        }
    }

    // A stable lock order prevents two concurrent orders [A, B] and [B, A] from deadlocking.
    private static List<StockLine> sortedByProduct(List<StockLine> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("No stock line provided");
        }
        for (StockLine line : lines) {
            if (line.productId() == null || line.quantity() == null || line.quantity() <= 0) {
                throw new IllegalArgumentException("Invalid stock line: " + line);
            }
        }
        return lines.stream().sorted(Comparator.comparing(StockLine::productId)).toList();
    }
}
