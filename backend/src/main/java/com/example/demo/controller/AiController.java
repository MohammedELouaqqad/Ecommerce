package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.AiService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@CrossOrigin(origins="http://localhost:5173")
@RestController
@RequestMapping("/api")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/customer/chat")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        try{
            return ResponseEntity.ok(aiService.chat(message));
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }
    

    
}
