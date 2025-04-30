package com.markendation.server.exceptions;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException() {
        super("Dish Not Found");
    }

    public DishNotFoundException(String message) {
        super(message);
    }
}