package com.markendation.server.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.markendation.server.dto.IngredientDto;
import com.markendation.server.exceptions.IngredientNotFoundException;
import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.IngredientRepository;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public IngredientDto addIngredient(IngredientDto dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.update(dto);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        IngredientDto response = new IngredientDto();
        response.update(savedIngredient);
        return response;
    }

    public IngredientDto getIngredient(String ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new IngredientNotFoundException());
        IngredientDto dto = new IngredientDto();
        dto.update(ingredient);

        return dto;
    }

    public List<IngredientDto> getPageIngredients(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Ingredient> ingredients = ingredientRepository.findAll(pageable).getContent();

        List<IngredientDto> result = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            IngredientDto dto = new IngredientDto();
            dto.update(ingredient);
            result.add(dto);
        }

        return result;
    }
}
