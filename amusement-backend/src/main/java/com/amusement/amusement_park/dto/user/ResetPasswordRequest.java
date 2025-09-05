package com.amusement.amusement_park.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordRequest {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must be at least 8 characters long, include uppercase, lowercase, number, and special character")
    private String newPassword;

    // Getters
    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
