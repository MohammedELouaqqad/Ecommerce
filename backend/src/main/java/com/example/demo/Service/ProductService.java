package com.example.demo.Service;

import com.example.demo.models.ProductEntity;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
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
}
