package com.markendation.server.classes;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.dto.IngredientDto;
import com.markendation.server.models.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreCalculation {
    private Store store;
    private List<ProductCost> products = new ArrayList<>();
    private List<IngredientDto> lackIngredients = new ArrayList<>();
    private List<ProductCost> similarProducts = new ArrayList<>();
    private Double totalCost = (double)0;
    private double distance;
    private float stars = 5;
    private double recently = 5;
    private double rating;
}
