package com.markendation.server.repositories.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.models.Ingredient;

public interface IngredientRepository extends MongoRepository<Ingredient, String> {
    
}
