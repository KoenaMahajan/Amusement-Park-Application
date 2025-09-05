package com.amusement.amusement_park.exception;

public class FoodItemNotFoundException extends RuntimeException {
    public FoodItemNotFoundException(Long id) {
        super("Food item with ID " + id + " not found.");
    }
}
