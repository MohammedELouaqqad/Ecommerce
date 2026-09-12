package com.example.demo.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtService;
import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request){
        var user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .role(request.getRole())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();


        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }


    
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var user = repository.findByEmail(request.getEmail())
            .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .user(user)
            .build();            
    }    



}

