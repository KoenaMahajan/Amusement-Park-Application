package com.amusement.amusement_park.entity.attraction;


import com.amusement.amusement_park.Enums.AlertType;
import com.amusement.amusement_park.Enums.Priority;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_alerts")
public class MaintenanceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    @NotNull(message = "Ride is required")
    @JsonBackReference
    private Ride ride;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    @NotNull(message = "Alert type is required")
    private AlertType alertType;

    @Column(name = "title", nullable = false, length = 200)
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @NotNull(message = "Priority is required")
    private Priority priority;

    @Column(name = "created_by", length = 100)
    @Size(max = 100, message = "Created by cannot exceed 100 characters")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public MaintenanceAlert() {}

    public MaintenanceAlert(Ride ride, AlertType alertType, String title, LocalDateTime startTime, Priority priority) {
        this.ride = ride;
        this.alertType = alertType;
        this.title = title;
        this.startTime = startTime;
        this.priority = priority;
    }

    // Business logic methods
    public boolean isCurrentlyActive() {
        if (!isActive) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isAfter(now)) {
            return false;
        }
        return endTime == null || endTime.isAfter(now);
    }

    public boolean isScheduledInFuture() {
        return isActive && startTime.isAfter(LocalDateTime.now());
    }

    public boolean hasEnded() {
        return endTime != null && endTime.isBefore(LocalDateTime.now());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}