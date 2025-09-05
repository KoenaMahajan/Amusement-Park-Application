package com.amusement.amusement_park.dto.attraction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FavoriteCreateDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Ride ID is required")
    private Long rideId;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public FavoriteCreateDto() {}

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}