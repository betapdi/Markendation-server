package com.markendation.server.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.markendation.server.dto.BasketDto;
import com.markendation.server.dto.DishDto;
import com.markendation.server.dto.IngredientDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Basket {
    @Id
    private String id;

    private String userId;

    private String basketName;

    @Builder.Default
    List<Ingredient> ingredients = new ArrayList<>();
    
    @Builder.Default
    List<Dish> dishes = new ArrayList<>();
    public void update(BasketDto basketDto) {
        if (basketDto.getId() != null) id = basketDto.getId();
        basketName = basketDto.getBasketName();
        ingredients.clear(); dishes.clear();
        for (DishDto dishDto : basketDto.getDishes()) {
            Dish dish = new Dish();
            dish.update(dishDto);
            dishes.add(dish);
        }

        for (IngredientDto ingredientDto : basketDto.getIngredients()) {
            Ingredient ingredient = new Ingredient();
            ingredient.update(ingredientDto);
            ingredients.add(ingredient);
        }
    }
}
