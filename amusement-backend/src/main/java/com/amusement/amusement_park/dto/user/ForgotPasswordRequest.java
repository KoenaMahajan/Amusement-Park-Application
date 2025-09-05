package com.amusement.amusement_park.dto.user;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    private String email;

    // Getters
    public String getEmail() {
        return email;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }
}
