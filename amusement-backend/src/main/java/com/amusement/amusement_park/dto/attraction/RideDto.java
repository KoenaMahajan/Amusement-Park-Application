package com.amusement.amusement_park.dto.attraction;


import com.amusement.amusement_park.Enums.ThrillLevel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class RideDto {
    private Long id;
    private String name;
    private String description;
    private ThrillLevel thrillLevel;
    private Integer minAge;
    private Integer maxAge;
    private Integer durationMinutes;
    private Integer heightRequirementCm;
    private String photoUrl;
    private String videoUrl;
    private Boolean isOperational;
    private Boolean isAvailable;
    private String locationDescription;
    private String safetyInstructions;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private List<MaintenanceAlertDto> activeMaintenanceAlerts;
    private Integer favoritesCount;
    private List<RidePhotoDto> photos;

    // Constructors
    public RideDto() {}

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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
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

    public List<MaintenanceAlertDto> getActiveMaintenanceAlerts() {
        return activeMaintenanceAlerts;
    }

    public void setActiveMaintenanceAlerts(List<MaintenanceAlertDto> activeMaintenanceAlerts) {
        this.activeMaintenanceAlerts = activeMaintenanceAlerts;
    }

    public Integer getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Integer favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public List<RidePhotoDto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<RidePhotoDto> photos) {
        this.photos = photos;
    }
}