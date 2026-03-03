package com.example.demo.controller;

import java.io.IOException;
import org.springframework.http.HttpHeaders;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Service.S3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin
@RequestMapping
@RestController
public class S3Controller {
    

    @Autowired
    private S3Service s3Service;

    @PostMapping("/api/customer/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile file) throws IOException{
        String filename = s3Service.uploadFile(file);
        return ResponseEntity.ok(filename);
    }


    @GetMapping("/api/customer/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) {
        byte[] data = s3Service.downloadFile(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+filename)
            .body(data);
    }
    
    
}
