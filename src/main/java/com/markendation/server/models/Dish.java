package com.markendation.server.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class Dish {
    @Id
    private String id;

    private String vietnameseName;

    private String name;

    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();

    private Integer quantity;

    public void update(DishDto dto) {
        vietnameseName = dto.getVietnameseName();
        name = dto.getName();
        quantity = dto.getQuantity();
        
        ingredients.clear();
        for (IngredientDto ingredientDto : dto.getIngredients()) {
            Ingredient ingredient = new Ingredient();
            ingredient.update(ingredientDto);

            ingredients.add(ingredient);
        }
    }
}
