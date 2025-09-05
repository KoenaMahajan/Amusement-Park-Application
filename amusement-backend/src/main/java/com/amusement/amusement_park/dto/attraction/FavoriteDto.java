package com.amusement.amusement_park.dto.attraction;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class FavoriteDto {
    private Long id;
    private Long userId;
    private RideDto ride;
    private String notes;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addedAt;

    // Constructors
    public FavoriteDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RideDto getRide() {
        return ride;
    }

    public void setRide(RideDto ride) {
        this.ride = ride;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}