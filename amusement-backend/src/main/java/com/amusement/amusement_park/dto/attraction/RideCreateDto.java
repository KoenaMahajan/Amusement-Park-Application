package com.amusement.amusement_park.dto.attraction;


import java.time.LocalDateTime;

import com.amusement.amusement_park.Enums.ThrillLevel;

import jakarta.validation.constraints.*;

public class RideCreateDto {
    
    @NotBlank(message = "Ride name is required")
    @Size(min = 2, max = 100, message = "Ride name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Thrill level is required")
    private ThrillLevel thrillLevel;

    @NotNull(message = "Minimum age is required")
    @Min(value = 0, message = "Minimum age cannot be negative")
    @Max(value = 100, message = "Minimum age cannot exceed 100")
    private Integer minAge;

    @Min(value = 0, message = "Maximum age cannot be negative")
    @Max(value = 120, message = "Maximum age cannot exceed 120")
    private Integer maxAge;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 120, message = "Duration cannot exceed 120 minutes")
    private Integer durationMinutes;

    @Min(value = 50, message = "Height requirement cannot be less than 50cm")
    @Max(value = 250, message = "Height requirement cannot exceed 250cm")
    private Integer heightRequirementCm;

    @Size(max = 500, message = "Photo URL cannot exceed 500 characters")
    private String photoUrl;

    @Size(max = 500, message = "Video URL cannot exceed 500 characters")
    private String videoUrl;

    @Size(max = 200, message = "Location description cannot exceed 200 characters")
    private String locationDescription;

    @Size(max = 2000, message = "Safety instructions cannot exceed 2000 characters")
    private String safetyInstructions;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;


    // Constructors
    public RideCreateDto() {}

    // Getters and Setters
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

        public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

}