package com.markendation.server.services;

import org.springframework.stereotype.Service;

import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.IngredientRepository;

@Service
public class SavingService {
    private static IngredientRepository ingredientRepository;

    public SavingService(IngredientRepository myIngredientRepository) {
        ingredientRepository = myIngredientRepository;
    }

    public static Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }
}
