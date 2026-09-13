package com.example.demo.dto;

import com.example.demo.models.User;

// What the API exposes about a user. Entities are never serialized directly,
// so the password hash (and any sensitive field added later) cannot leak.
public record UserResponse(Long id, String fullName, String email, String role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
