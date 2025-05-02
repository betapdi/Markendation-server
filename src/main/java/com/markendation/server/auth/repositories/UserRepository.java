package com.markendation.server.auth.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.markendation.server.auth.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
     
    Optional<User> findByEmail(String email);
}
