package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used only by the Admin-only endpoint: unlike RegisterRequest, it carries a role.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRegisterRequest {
    private String fullName;
    private String email;
    private String role;
    private String password;
}
