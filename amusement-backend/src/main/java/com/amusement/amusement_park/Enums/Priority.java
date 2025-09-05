package com.amusement.amusement_park.Enums;

public enum Priority {
    LOW("Low priority - informational"),
    MEDIUM("Medium priority - plan accordingly"),
    HIGH("High priority - significant impact"),
    CRITICAL("Critical priority - immediate attention required");

    private final String description;

    Priority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
