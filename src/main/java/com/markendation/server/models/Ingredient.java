package com.markendation.server.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class Ingredient {
    @Id
    private String id;

    private String vietnameseName;

    private String name;

    private String unit;
    
    private float quantity;

    private String category;

    private String imageUrl;

    private List<String> token_ngrams;

    public void update(IngredientDto dto) {
        if (dto.getId() != null) id = dto.getId();
        vietnameseName = dto.getVietnameseName();
        name = dto.getName();
        unit = dto.getUnit();
        quantity = dto.getQuantity();
        imageUrl = dto.getImageUrl();
        category = dto.getCategory();
    }
}
