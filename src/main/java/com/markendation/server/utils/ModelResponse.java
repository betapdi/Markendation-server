package com.markendation.server.utils;

import java.util.List;

import org.springframework.data.util.Pair;

import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModelResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class IngredientResponse {
        String ingredient_name;
        String total_unit;
        String category;

        public Ingredient toIngredient() {
            Ingredient ingredient = new Ingredient();
            ingredient.setCategory(category);
            
            Pair<Integer, String> value = UnitQuantityParser.parseQuantity(total_unit);
            ingredient.setQuantity(value.getFirst());
            ingredient.setUnit(value.getSecond());

            ingredient.setName(ingredient_name);
            ingredient.setVietnameseName(ingredient_name);

            return ingredient;
        }
    };

    private String correlationId;
    private String name;
    private String imageUrl;
    private int servings;
    private List<IngredientResponse> ingredients;

    public Dish toDish() {
        Dish result = new Dish();
        result.setImageUrl(imageUrl);
        result.setName(name);
        result.setVietnameseName(name);
        result.setServings(servings);

        for (IngredientResponse ingredientResponse : ingredients) {
            Ingredient ingredient = ingredientResponse.toIngredient();
            result.getIngredients().add(ingredient);
        }
        
        return result;
    }
}
