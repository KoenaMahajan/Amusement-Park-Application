package com.amusement.amusement_park.dto.user;

public class UserProfile {
    private String email;
    private String role;
    private boolean verified;
    private String name;
    private String phoneNumber;

    // Getters
    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
