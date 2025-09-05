package com.amusement.amusement_park.entity.feedback;

import com.amusement.amusement_park.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer rideRating;
    private Integer cleanlinessRating;
    private Integer staffBehaviorRating;
    private Integer foodQualityRating;
    private String comments;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters
    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
