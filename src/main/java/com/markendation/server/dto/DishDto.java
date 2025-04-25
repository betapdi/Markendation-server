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

    private Integer quantity;

    public void update(Dish dish) {
        ingredients.clear();

        for (Ingredient ingredient : dish.getIngredients()) {
            IngredientDto dto = new IngredientDto();
            dto.update(ingredient);
            ingredients.add(dto);
        }

        quantity = dish.getQuantity();
        name = dish.getName();
        vietnameseName = dish.getVietnameseName();
    }
}
