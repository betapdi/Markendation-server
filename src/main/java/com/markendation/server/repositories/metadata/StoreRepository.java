package com.markendation.server.repositories.metadata;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.models.Store;

public interface StoreRepository extends MongoRepository<Store, String> {
}
