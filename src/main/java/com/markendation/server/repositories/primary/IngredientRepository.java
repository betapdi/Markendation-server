package com.markendation.server.repositories.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.markendation.server.models.Ingredient;

public interface IngredientRepository extends MongoRepository<Ingredient, String> {
    @Query("{ 'vietnameseName': { $regex: ?0, $options: 'i' } }")
    Page<Ingredient> findByVietnameseNameRegexIgnoreCase(String regex, Pageable pageable);
}
