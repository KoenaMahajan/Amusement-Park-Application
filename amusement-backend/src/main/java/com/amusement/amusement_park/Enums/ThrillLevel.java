package com.amusement.amusement_park.Enums;

public enum ThrillLevel {
    LOW("Family-friendly, suitable for all ages"),
    MEDIUM("Moderate excitement, some restrictions may apply"),
    HIGH("High thrill experience, age and height restrictions"),
    EXTREME("Maximum thrill experience, strict restrictions");

    private final String description;

    ThrillLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
