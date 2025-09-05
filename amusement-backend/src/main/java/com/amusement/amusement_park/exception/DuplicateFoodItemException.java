package com.amusement.amusement_park.exception;

public class DuplicateFoodItemException extends RuntimeException {
    public DuplicateFoodItemException(String name) {
        super("Food item with name '" + name + "' already exists. Please use a different name.");
    }
}
