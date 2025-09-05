package com.amusement.amusement_park.exception.attraction;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(String message) {
        super(message);
    }
}