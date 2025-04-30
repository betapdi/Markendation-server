package com.markendation.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.markendation.server.dto.DishDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.services.DishService;
import com.markendation.server.services.IngredientService;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/v1/public")
public class PublicController {
    private final IngredientService ingredientService;
    private final DishService dishService;

    public PublicController(IngredientService ingredientService, DishService dishService) {
        this.ingredientService = ingredientService;
        this.dishService = dishService;
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientDto> getIngredient(@PathVariable("id") String ingredientId) throws IOException {
        IngredientDto response = ingredientService.getIngredient(ingredientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientDto>> getPageIngredients(@RequestParam(defaultValue = "0") Integer pageNo, 
                                        @RequestParam(defaultValue = "30") Integer pageSize) {
        List<IngredientDto> response = ingredientService.getPageIngredients(pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<DishDto> getDish(@PathVariable("id") String dishId) throws IOException {
        DishDto response = dishService.getDish(dishId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dishes")
    public ResponseEntity<List<DishDto>> getPageDishes(@RequestParam(defaultValue = "0") Integer pageNo, 
                                        @RequestParam(defaultValue = "30") Integer pageSize) {
        List<DishDto> response = dishService.getPageDishes(pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
