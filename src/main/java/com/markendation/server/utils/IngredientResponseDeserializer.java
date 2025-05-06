package com.markendation.server.utils;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.markendation.server.classes.ModelResponse;

public class IngredientResponseDeserializer extends JsonDeserializer<ModelResponse.IngredientResponse> {

    @Override
    public ModelResponse.IngredientResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        String name = node.get("ingredient_name").asText();
        String category = node.get("category").asText();

        name = name.replaceAll("\\s*\\(.*?\\)", ""); 

        // Try both field names for total_unit
        JsonNode totalUnitNode = node.get("total unit");
        if (totalUnitNode == null) {
            totalUnitNode = node.get("total_unit");
        }

        String totalUnit = totalUnitNode != null ? totalUnitNode.asText() : "";

        return new ModelResponse.IngredientResponse(name, totalUnit, category);
    }
}