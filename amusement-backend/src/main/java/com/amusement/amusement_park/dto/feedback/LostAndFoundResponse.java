package com.amusement.amusement_park.dto.feedback;

import com.amusement.amusement_park.Enums.LostFoundStatus;

import java.time.LocalDateTime;

public class LostAndFoundResponse {
    private Long id;
    private String itemName;
    private String description;
    private LostFoundStatus status;
    private String location;
    private String reportedBy;
    private LocalDateTime reportedAt;

    // Default constructor
    public LostAndFoundResponse() {
    }

    // All-args constructor
    public LostAndFoundResponse(Long id, String itemName, String description, LostFoundStatus status, String location,
            String reportedBy, LocalDateTime reportedAt) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.status = status;
        this.location = location;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
    }

    // Builder pattern
    public static LostAndFoundResponseBuilder builder() {
        return new LostAndFoundResponseBuilder();
    }

    public static class LostAndFoundResponseBuilder {
        private Long id;
        private String itemName;
        private String description;
        private LostFoundStatus status;
        private String location;
        private String reportedBy;
        private LocalDateTime reportedAt;

        public LostAndFoundResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LostAndFoundResponseBuilder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public LostAndFoundResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public LostAndFoundResponseBuilder status(LostFoundStatus status) {
            this.status = status;
            return this;
        }

        public LostAndFoundResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public LostAndFoundResponseBuilder reportedBy(String reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }

        public LostAndFoundResponseBuilder reportedAt(LocalDateTime reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }

        public LostAndFoundResponse build() {
            return new LostAndFoundResponse(id, itemName, description, status, location, reportedBy, reportedAt);
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

    public String getReportedBy() {
        return reportedBy;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
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

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }
}
