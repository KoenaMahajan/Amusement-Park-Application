package com.amusement.amusement_park.dto.feedback;

import com.amusement.amusement_park.Enums.IssueStatus;

import java.time.LocalDateTime;

public class IssueResponse {
    private Long id;
    private String subject;
    private String description;
    private IssueStatus status;
    private String reportedBy;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    // Default constructor
    public IssueResponse() {
    }

    // All-args constructor
    public IssueResponse(Long id, String subject, String description, IssueStatus status, String reportedBy,
            LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.reportedBy = reportedBy;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    // Builder pattern
    public static IssueResponseBuilder builder() {
        return new IssueResponseBuilder();
    }

    public static class IssueResponseBuilder {
        private Long id;
        private String subject;
        private String description;
        private IssueStatus status;
        private String reportedBy;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;

        public IssueResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IssueResponseBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public IssueResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public IssueResponseBuilder status(IssueStatus status) {
            this.status = status;
            return this;
        }

        public IssueResponseBuilder reportedBy(String reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }

        public IssueResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IssueResponseBuilder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public IssueResponse build() {
            return new IssueResponse(id, subject, description, status, reportedBy, createdAt, resolvedAt);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}