package com.markendation.server.repositories.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.markendation.server.models.Dish;

public interface DishRepository extends MongoRepository<Dish, String> {
    @Query("{ 'vietnameseName': { $regex: ?0, $options: 'i' } }")
    Page<Dish> findByVietnameseNameRegexIgnoreCase(String regex, Pageable pageable);
}
