package com.markendation.server.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.markendation.server.auth.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
     
    Optional<User> findByEmail(String email);
}
