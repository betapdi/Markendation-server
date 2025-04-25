package com.markendation.server.exceptions;

public class IngredientNotFoundException extends RuntimeException {
    public IngredientNotFoundException() {
        super("Ingredient Not Found");
    }

    public IngredientNotFoundException(String message) {
        super(message);
    }
}