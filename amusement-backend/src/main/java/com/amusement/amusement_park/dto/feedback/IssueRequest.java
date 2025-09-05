package com.amusement.amusement_park.dto.feedback;

import jakarta.validation.constraints.NotBlank;

public class IssueRequest {
    @NotBlank
    private String subject;

    @NotBlank
    private String description;

    // Getters
    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}