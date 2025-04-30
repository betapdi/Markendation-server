package com.markendation.server.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.markendation.server.dto.DishDto;
import com.markendation.server.exceptions.DishNotFoundException;
import com.markendation.server.models.Dish;
import com.markendation.server.repositories.primary.DishRepository;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public DishDto addDish(DishDto dto) {
        Dish dish = new Dish();
        dish.update(dto);
        Dish savedDish = dishRepository.save(dish);

        DishDto response = new DishDto();
        response.update(savedDish);
        return response;
    }

    public DishDto getDish(String dishId) {
        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new DishNotFoundException());
        DishDto response = new DishDto();
        response.update(dish);
        
        return response;
    }

    public List<DishDto> getPageDishes(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Dish> dishes = dishRepository.findAll(pageable).getContent();

        List<DishDto> response = new ArrayList<>();
        for (Dish dish : dishes) {
            DishDto dto = new DishDto();
            dto.update(dish);
            response.add(dto);
        }

        return response;
    }
}
