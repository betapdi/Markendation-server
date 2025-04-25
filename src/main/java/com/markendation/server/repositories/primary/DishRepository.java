package com.markendation.server.repositories.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.models.Dish;

public interface DishRepository extends MongoRepository<Dish, String> {
    
}
