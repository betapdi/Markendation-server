package com.markendation.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.markendation.server.dto.IngredientDto;
import com.markendation.server.services.DishService;
import com.markendation.server.services.IngredientService;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    private final IngredientService ingredientService;

    public AdminController(IngredientService ingredientService, DishService dishService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping("/ingredient/add")
    public ResponseEntity<IngredientDto> getIngredient(@RequestBody IngredientDto dto) throws IOException {
        IngredientDto response = ingredientService.addIngredient(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
