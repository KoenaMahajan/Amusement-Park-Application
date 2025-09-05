package com.amusement.amusement_park.exception.attraction;

public class FavoriteAlreadyExistsException extends RuntimeException {
    public FavoriteAlreadyExistsException(String message) {
        super(message);
    }
}