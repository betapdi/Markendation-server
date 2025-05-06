package com.markendation.server.classes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCleaned {
    private String ingredient_name;
    private String unit;
    private String image;
    private String vietnamese_name;
    private String category;

    // Getters and Setters
    public String getName() { return ingredient_name; }
    public void setName(String name) { this.ingredient_name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getVietnamese_name() { return vietnamese_name; }
    public void setVietnamese_name(String vietnamese_name) { this.vietnamese_name = vietnamese_name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
