package com.amusement.amusement_park.entity.attraction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorites", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ride_id"}))
public class UserFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    @NotNull(message = "Ride is required")
    @JsonBackReference
    private Ride ride;

    @CreationTimestamp
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(name = "notes", length = 500)
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public UserFavorite() {}

    public UserFavorite(Long userId, Ride ride) {
        this.userId = userId;
        this.ride = ride;
    }

    public UserFavorite(Long userId, Ride ride, String notes) {
        this.userId = userId;
        this.ride = ride;
        this.notes = notes;
    }

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

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}