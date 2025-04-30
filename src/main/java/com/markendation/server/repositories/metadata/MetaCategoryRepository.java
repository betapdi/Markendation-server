package com.markendation.server.repositories.metadata;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.models.MetaCategory;

public interface MetaCategoryRepository extends MongoRepository<MetaCategory, String> {
    Optional<MetaCategory> findByCategory(String category);
}
