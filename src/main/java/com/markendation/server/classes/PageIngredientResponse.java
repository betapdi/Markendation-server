package com.markendation.server.classes;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.dto.IngredientDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageIngredientResponse {
    private Integer numIngredients;
    private List<IngredientDto> ingredients = new ArrayList<>();
}
