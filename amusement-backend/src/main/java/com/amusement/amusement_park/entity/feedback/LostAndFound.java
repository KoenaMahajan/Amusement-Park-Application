package com.amusement.amusement_park.entity.feedback;

import com.amusement.amusement_park.Enums.LostFoundStatus;
import com.amusement.amusement_park.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class LostAndFound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    private String description;

    @Enumerated(EnumType.STRING)
    private LostFoundStatus status; // LOST, FOUND, RETURNED

    private String location;

    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reportedBy;

    // Default constructor
    public LostAndFound() {
    }

    // All-args constructor
    public LostAndFound(Long id, String itemName, String description, LostFoundStatus status, String location,
            LocalDateTime reportedAt, User reportedBy) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.status = status;
        this.location = location;
        this.reportedAt = reportedAt;
        this.reportedBy = reportedBy;
    }

    // Builder pattern
    public static LostAndFoundBuilder builder() {
        return new LostAndFoundBuilder();
    }

    public static class LostAndFoundBuilder {
        private Long id;
        private String itemName;
        private String description;
        private LostFoundStatus status;
        private String location;
        private LocalDateTime reportedAt;
        private User reportedBy;

        public LostAndFoundBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LostAndFoundBuilder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public LostAndFoundBuilder description(String description) {
            this.description = description;
            return this;
        }

        public LostAndFoundBuilder status(LostFoundStatus status) {
            this.status = status;
            return this;
        }

        public LostAndFoundBuilder location(String location) {
            this.location = location;
            return this;
        }

        public LostAndFoundBuilder reportedAt(LocalDateTime reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }

        public LostAndFoundBuilder reportedBy(User reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }

        public LostAndFound build() {
            return new LostAndFound(id, itemName, description, status, location, reportedAt, reportedBy);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public LostFoundStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(LostFoundStatus status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }
}
