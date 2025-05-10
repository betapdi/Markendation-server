package com.markendation.server.classes;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;
import com.markendation.server.models.Product;
import com.markendation.server.repositories.primary.IngredientRepository;
import com.markendation.server.services.CalculatingService;
import com.markendation.server.services.SavingService;
import com.markendation.server.services.SearchingService;
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
    public static class IngredientResponse {
        String ingredient_name;
        String ingredient_vn_name;
        String total_unit;
        String category;

        public Ingredient toIngredient() {
            Ingredient matchedIngredient = SearchingService.searchIngredientByPattern(ingredient_name);
            Ingredient ingredient = new Ingredient();

            ingredient.setCategory(category);
            
            Pair<Integer, String> value = UnitQuantityParser.parseQuantity(total_unit);
            ingredient.setQuantity(value.getFirst());
            ingredient.setUnit(value.getSecond());

            ingredient.setName(ingredient_name);
            ingredient.setVietnameseName(ingredient_vn_name);

            if (matchedIngredient.getId() != null) {
                ingredient.setId(matchedIngredient.getId());
                ingredient.setImageUrl(matchedIngredient.getImageUrl());
            }

            return ingredient;
        }
    };

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class DishResponse {
        String dish_name;   
        String dish_vn_name;
        Integer servings = 1;

        List<IngredientResponse> ingredients_must_have;

        List<IngredientResponse> ingredients_optional;

        public Dish toDish() {
            Dish result = new Dish();
            result.setId(UUID.randomUUID().toString());
            result.setName(dish_name);
            result.setVietnameseName(dish_vn_name);
            result.setServings(servings);
    
            for (IngredientResponse ingredientResponse : ingredients_must_have) {
                Ingredient ingredient = ingredientResponse.toIngredient();
                if (ingredient.getId() == null) {
                    Product product = CalculatingService.findProductByIngredient(ingredient);
                    ingredient.setImageUrl(product.getImage());

                    if (ingredient.getImageUrl() != null) {
                        ingredient = SavingService.saveIngredient(ingredient);
                        result.getIngredients().add(ingredient);
                    }
                }

                else result.getIngredients().add(ingredient);
            }

            for (IngredientResponse ingredientResponse : ingredients_optional) {
                Ingredient ingredient = ingredientResponse.toIngredient();
                if (ingredient.getId() == null) {
                    Product product = CalculatingService.findProductByIngredient(ingredient);
                    ingredient.setImageUrl(product.getImage());

                    if (ingredient.getImageUrl() != null) {
                        ingredient = SavingService.saveIngredient(ingredient);
                        result.getOptionalIngredients().add(ingredient);
                    }
                }

                else result.getOptionalIngredients().add(ingredient);
            }
            
            return result;
        }
    };

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class TempIngredients {
        List<DishResponse> dishes;
    }

    private String correlationId;
    private String dish;
    private TempIngredients ingredients;
    private String modelType;
}
