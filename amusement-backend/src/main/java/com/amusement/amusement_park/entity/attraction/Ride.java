package com.amusement.amusement_park.entity.attraction;


import com.amusement.amusement_park.Enums.ThrillLevel;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Ride name is required")
    @Size(min = 2, max = 100, message = "Ride name must be between 2 and 100 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "thrill_level", nullable = false)
    @NotNull(message = "Thrill level is required")
    private ThrillLevel thrillLevel;

    @Column(name = "min_age", nullable = false)
    @NotNull(message = "Minimum age is required")
    @Min(value = 0, message = "Minimum age cannot be negative")
    @Max(value = 100, message = "Minimum age cannot exceed 100")
    private Integer minAge;

    @Column(name = "max_age")
    @Min(value = 0, message = "Maximum age cannot be negative")
    @Max(value = 120, message = "Maximum age cannot exceed 120")
    private Integer maxAge;

    @Column(name = "duration_minutes", nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 120, message = "Duration cannot exceed 120 minutes")
    private Integer durationMinutes;

    @Column(name = "height_requirement_cm")
    @Min(value = 50, message = "Height requirement cannot be less than 50cm")
    @Max(value = 250, message = "Height requirement cannot exceed 250cm")
    private Integer heightRequirementCm;

    @Column(name = "photo_url", length = 500)
    @Size(max = 500, message = "Photo URL cannot exceed 500 characters")
    private String photoUrl;

    @Column(name = "video_url", length = 500)
    @Size(max = 500, message = "Video URL cannot exceed 500 characters")
    private String videoUrl;

    @Column(name = "is_operational", nullable = false)
    private Boolean isOperational = true;

    @Column(name = "location_description", length = 200)
    @Size(max = 200, message = "Location description cannot exceed 200 characters")
    private String locationDescription;

    @Column(name = "safety_instructions", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Safety instructions cannot exceed 2000 characters")
    private String safetyInstructions;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MaintenanceAlert> maintenanceAlerts = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserFavorite> userFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<RidePhoto> ridePhotos = new ArrayList<>();

    // Constructors
    public Ride() {}

    public Ride(String name, String description, ThrillLevel thrillLevel, Integer minAge, Integer durationMinutes) {
        this.name = name;
        this.description = description;
        this.thrillLevel = thrillLevel;
        this.minAge = minAge;
        this.durationMinutes = durationMinutes;
    }

    // Business logic methods
    public boolean isSuitableForAge(int age) {
        if (age < minAge) {
            return false;
        }
        return maxAge == null || age <= maxAge;
    }

    public boolean isSuitableForHeight(int heightCm) {
        return heightRequirementCm == null || heightCm >= heightRequirementCm;
    }

    public boolean isAvailable() {
        return isOperational && 
               maintenanceAlerts.stream()
                   .noneMatch(alert -> alert.getIsActive() && 
                             alert.getStartTime().isBefore(LocalDateTime.now()) &&
                             (alert.getEndTime() == null || alert.getEndTime().isAfter(LocalDateTime.now())));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ThrillLevel getThrillLevel() {
        return thrillLevel;
    }

    public void setThrillLevel(ThrillLevel thrillLevel) {
        this.thrillLevel = thrillLevel;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getHeightRequirementCm() {
        return heightRequirementCm;
    }

    public void setHeightRequirementCm(Integer heightRequirementCm) {
        this.heightRequirementCm = heightRequirementCm;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Boolean getIsOperational() {
        return isOperational;
    }

    public void setIsOperational(Boolean isOperational) {
        this.isOperational = isOperational;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getSafetyInstructions() {
        return safetyInstructions;
    }

    public void setSafetyInstructions(String safetyInstructions) {
        this.safetyInstructions = safetyInstructions;
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

    public List<MaintenanceAlert> getMaintenanceAlerts() {
        return maintenanceAlerts;
    }

    public void setMaintenanceAlerts(List<MaintenanceAlert> maintenanceAlerts) {
        this.maintenanceAlerts = maintenanceAlerts;
    }

    public List<UserFavorite> getUserFavorites() {
        return userFavorites;
    }

    public void setUserFavorites(List<UserFavorite> userFavorites) {
        this.userFavorites = userFavorites;
    }

    public List<RidePhoto> getRidePhotos() {
        return ridePhotos;
    }

    public void setRidePhotos(List<RidePhoto> ridePhotos) {
        this.ridePhotos = ridePhotos;
    }
}