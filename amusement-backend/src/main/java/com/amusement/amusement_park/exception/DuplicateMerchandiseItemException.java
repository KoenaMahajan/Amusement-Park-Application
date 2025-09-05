package com.amusement.amusement_park.exception;

public class DuplicateMerchandiseItemException extends RuntimeException {
    public DuplicateMerchandiseItemException(String name) {
        super("Merchandise item with name '" + name + "' already exists. Please use a different name.");
    }
}
