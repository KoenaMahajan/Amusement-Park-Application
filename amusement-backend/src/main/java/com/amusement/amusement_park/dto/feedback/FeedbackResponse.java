package com.amusement.amusement_park.dto.feedback;

import java.time.LocalDateTime;

public class FeedbackResponse {
    private Integer id;
    private String userName;
    private Integer rideRating;
    private Integer cleanlinessRating;
    private Integer staffBehaviorRating;
    private Integer foodQualityRating;
    private String comments;
    private LocalDateTime createdAt;

    public Integer getCleanlinessRating() {
        return cleanlinessRating;
    }

    public void setCleanlinessRating(Integer cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getFoodQualityRating() {
        return foodQualityRating;
    }

    public void setFoodQualityRating(Integer foodQualityRating) {
        this.foodQualityRating = foodQualityRating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRideRating() {
        return rideRating;
    }

    public void setRideRating(Integer rideRating) {
        this.rideRating = rideRating;
    }

    public Integer getStaffBehaviorRating() {
        return staffBehaviorRating;
    }

    public void setStaffBehaviorRating(Integer staffBehaviorRating) {
        this.staffBehaviorRating = staffBehaviorRating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
