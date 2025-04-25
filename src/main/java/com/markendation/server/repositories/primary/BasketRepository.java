package com.markendation.server.repositories.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.models.Basket;

public interface BasketRepository extends MongoRepository<Basket, String> {
    
}
