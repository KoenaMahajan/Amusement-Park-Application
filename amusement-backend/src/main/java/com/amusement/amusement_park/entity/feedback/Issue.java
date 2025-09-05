package com.amusement.amusement_park.entity.feedback;

import com.amusement.amusement_park.Enums.IssueStatus;
import com.amusement.amusement_park.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String description;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reportedBy;

    // Default constructor
    public Issue() {
    }

    // All-args constructor
    public Issue(Long id, String subject, String description, IssueStatus status, LocalDateTime createdAt,
            LocalDateTime resolvedAt, User reportedBy) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.reportedBy = reportedBy;
    }

    // Builder pattern
    public static IssueBuilder builder() {
        return new IssueBuilder();
    }

    public static class IssueBuilder {
        private Long id;
        private String subject;
        private String description;
        private IssueStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;
        private User reportedBy;

        public IssueBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IssueBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public IssueBuilder description(String description) {
            this.description = description;
            return this;
        }

        public IssueBuilder status(IssueStatus status) {
            this.status = status;
            return this;
        }

        public IssueBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IssueBuilder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public IssueBuilder reportedBy(User reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }

        public Issue build() {
            return new Issue(id, subject, description, status, createdAt, resolvedAt, reportedBy);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public User getReportedBy() {
        return reportedBy;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }
}
