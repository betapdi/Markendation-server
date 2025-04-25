package com.markendation.server.dto;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.models.Basket;
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
public class BasketDto {
    private String id;

    private String userId;

    private List<IngredientDto> ingredients = new ArrayList<>();

    private List<DishDto> dishes = new ArrayList<>();

    public void update(Basket basket) {
        ingredients.clear(); dishes.clear();
        for (Dish dish : basket.getDishes()) {
            DishDto dto = new DishDto();
            dto.update(dish);
            dishes.add(dto);
        }

        for (Ingredient ingredient : basket.getIngredients()) {
            IngredientDto dto = new IngredientDto();
            dto.update(ingredient);
            ingredients.add(dto);
        }
    }
}
