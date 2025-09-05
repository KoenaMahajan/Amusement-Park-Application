package com.amusement.amusement_park.dto.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FeedbackRequest {

    @NotNull(message = "Ride rating is required")
    @Min(value = 1, message = "Ride rating must be at least 1")
    @Max(value = 5, message = "Ride rating must be at most 5")
    private Integer rideRating;

    @NotNull(message = "Cleanliness rating is required")
    @Min(value = 1, message = "Cleanliness rating must be at least 1")
    @Max(value = 5, message = "Cleanliness rating must be at most 5")
    private Integer cleanlinessRating;

    @NotNull(message = "Staff behavior rating is required")
    @Min(value = 1, message = "Staff behavior rating must be at least 1")
    @Max(value = 5, message = "Staff behavior rating must be at most 5")
    private Integer staffBehaviorRating;

    @NotNull(message = "Food quality rating is required")
    @Min(value = 1, message = "Food quality rating must be at least 1")
    @Max(value = 5, message = "Food quality rating must be at most 5")
    private Integer foodQualityRating;

    private String comments; // optional

    // Getters
    public Integer getRideRating() {
        return rideRating;
    }

    public Integer getCleanlinessRating() {
        return cleanlinessRating;
    }

    public Integer getStaffBehaviorRating() {
        return staffBehaviorRating;
    }

    public Integer getFoodQualityRating() {
        return foodQualityRating;
    }

    public String getComments() {
        return comments;
    }

    // Setters
    public void setRideRating(Integer rideRating) {
        this.rideRating = rideRating;
    }

    public void setCleanlinessRating(Integer cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }

    public void setStaffBehaviorRating(Integer staffBehaviorRating) {
        this.staffBehaviorRating = staffBehaviorRating;
    }

    public void setFoodQualityRating(Integer foodQualityRating) {
        this.foodQualityRating = foodQualityRating;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
