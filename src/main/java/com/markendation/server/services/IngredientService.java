package com.markendation.server.services;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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

    public List<IngredientDto> getPageIngredients(int pageNo, int pageSize, String pattern) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        
        Page<Ingredient> page;
        if (pattern == null || pattern.isBlank()) {
            page = ingredientRepository.findAll(pageable);
        } else {
            String regex = ".*" + Pattern.quote(pattern) + ".*";
            page = ingredientRepository.findByVietnameseNameRegexIgnoreCase(regex, pageable);
        }

        return page.getContent().stream().map(ingredient -> {
            IngredientDto dto = new IngredientDto();
            dto.update(ingredient);
            return dto;
        }).collect(Collectors.toList());
    }

    public Integer getTotal() {
        return (int)ingredientRepository.count();
    }
}
