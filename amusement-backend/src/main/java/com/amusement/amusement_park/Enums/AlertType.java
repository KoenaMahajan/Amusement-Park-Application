package com.amusement.amusement_park.Enums;

public enum AlertType {
    SCHEDULED_MAINTENANCE("Planned maintenance work"),
    EMERGENCY_CLOSURE("Emergency closure due to technical issues"),
    WEATHER_CLOSURE("Closed due to weather conditions"),
    INSPECTION("Safety inspection in progress");

    private final String description;

    AlertType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
