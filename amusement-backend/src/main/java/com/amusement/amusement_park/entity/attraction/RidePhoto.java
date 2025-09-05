package com.amusement.amusement_park.entity.attraction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_photos")
public class RidePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    @NotNull(message = "Ride is required")
    @JsonBackReference
    private Ride ride;

    @Column(name = "photo_url", nullable = false, length = 500)
    @NotBlank(message = "Photo URL is required")
    @Size(max = 500, message = "Photo URL cannot exceed 500 characters")
    private String photoUrl;

    @Column(name = "caption", length = 200)
    @Size(max = 200, message = "Caption cannot exceed 200 characters")
    private String caption;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    // Constructors
    public RidePhoto() {}

    public RidePhoto(Ride ride, String photoUrl) {
        this.ride = ride;
        this.photoUrl = photoUrl;
    }

    public RidePhoto(Ride ride, String photoUrl, String caption, Boolean isPrimary) {
        this.ride = ride;
        this.photoUrl = photoUrl;
        this.caption = caption;
        this.isPrimary = isPrimary;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}