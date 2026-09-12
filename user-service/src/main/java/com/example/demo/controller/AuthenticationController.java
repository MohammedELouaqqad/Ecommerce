package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.AuthenticationService;
import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.models.User;

import com.example.demo.repository.UserRepository;


import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    private final AuthenticationService service;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    

    @GetMapping("/admin/allUsers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/admin/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        try{
            Optional <User> user = userRepository.findById(id);
            if(user.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deteled with Success");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }
    
    
    
}
