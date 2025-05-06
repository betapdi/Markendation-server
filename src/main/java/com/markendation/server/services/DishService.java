package com.markendation.server.services;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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

    public List<DishDto> getPageDishes(Integer pageNo, Integer pageSize, String pattern) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        
        Page<Dish> page;
        if (pattern == null || pattern.isBlank()) {
            page = dishRepository.findAll(pageable);
        } else {
            String regex = ".*" + Pattern.quote(pattern) + ".*";
            page = dishRepository.findByVietnameseNameRegexIgnoreCase(regex, pageable);
        }

        return page.getContent().stream().map(dish -> {
            DishDto dto = new DishDto();
            dto.update(dish);
            return dto;
        }).collect(Collectors.toList());
    }

    public Integer getTotal() {
        return (int)dishRepository.count();
    }
}
