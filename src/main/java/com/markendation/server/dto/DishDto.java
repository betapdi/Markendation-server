package com.markendation.server.dto;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DishDto {
    private String id;

    private String vietnameseName;

    private String name;

    private List<IngredientDto> ingredients = new ArrayList<>();

    private List<IngredientDto> optionalIngredients = new ArrayList<>();

    private Integer servings;

    private String imageUrl;

    public void update(Dish dish) {
        ingredients.clear();
        for (Ingredient ingredient : dish.getIngredients()) {
            IngredientDto dto = new IngredientDto();
            dto.update(ingredient);
            ingredients.add(dto);
        }

        optionalIngredients.clear();
        for (Ingredient ingredient : dish.getOptionalIngredients()) {
            IngredientDto dto = new IngredientDto();
            dto.update(ingredient);
            optionalIngredients.add(dto);
        }

        servings = dish.getServings();
        name = dish.getName();
        vietnameseName = dish.getVietnameseName();
        imageUrl = dish.getImageUrl();
    }
}
