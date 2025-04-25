package com.markendation.server.dto;

import com.markendation.server.models.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IngredientDto {
    private String id;

    private String vietnameseName;

    private String name;

    private String unit;

    private float quantity;

    public void update(Ingredient ingredient) {
        id = ingredient.getId();
        vietnameseName = ingredient.getVietnameseName();
        name = ingredient.getName();
        unit = ingredient.getUnit();
        quantity = ingredient.getQuantity();
    }
}
