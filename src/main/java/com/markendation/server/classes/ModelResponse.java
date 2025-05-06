package com.markendation.server.classes;

import java.util.List;

import org.springframework.data.util.Pair;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;
import com.markendation.server.utils.IngredientResponseDeserializer;
import com.markendation.server.utils.UnitQuantityParser;

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
    @JsonDeserialize(using = IngredientResponseDeserializer.class)
    public static class IngredientResponse {
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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class TempIngredients {
        List<IngredientResponse> ingredients;
    }

    private String correlationId;
    private String dish;
    private String imageUrl;
    private int servings = 1;
    private TempIngredients ingredients;
    private String modelType;

    public Dish toDish() {
        Dish result = new Dish();
        result.setImageUrl(imageUrl);
        result.setName(dish);
        result.setVietnameseName(dish);
        result.setServings(servings);

        for (IngredientResponse ingredientResponse : ingredients.getIngredients()) {
            Ingredient ingredient = ingredientResponse.toIngredient();
            result.getIngredients().add(ingredient);
        }
        
        return result;
    }
}
