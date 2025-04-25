package com.markendation.server.repositories.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    
}
