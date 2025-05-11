package com.markendation.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.DishRepository;
import com.markendation.server.repositories.primary.IngredientRepository;
import com.markendation.server.utils.TokenUtils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EnableAsync
@SpringBootApplication
public class ServerApplication {

    private final DishRepository dishRepository;

    ServerApplication(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(MongoTemplate mongoTemplate, IngredientRepository ingredientRepository) {
		return args -> {
			// String currentDir = System.getProperty("user.dir");
			// String jsonFilePath = Paths.get(currentDir, "src", "main", "java", "com", "markendation", "server", "dishes_data.json").toString();
			// String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)), java.nio.charset.StandardCharsets.UTF_8);
			// ObjectMapper mapper = new ObjectMapper();
			// try {
			// 	JsonNode root = mapper.readTree(content);

			// 	for (JsonNode dishNode : root) {
			// 		Dish dish = new Dish();
			// 		dish.setName(dishNode.get("dish").asText());
			// 		dish.setVietnameseName(dishNode.get("vietnamese_name").asText());
			// 		dish.setImageUrl(dishNode.get("image").asText());

			// 		List<Ingredient> ingredients = new ArrayList<>();
			// 		for (JsonNode ingNode : dishNode.get("ingredients")) {
			// 			Ingredient ing = new Ingredient();
			// 			ing.setName(ingNode.get("ingredient_name").asText());
			// 			ing.setVietnameseName(ingNode.get("vietnamese_name").asText());
			// 			ing.setCategory(ingNode.get("category").asText());
			// 			ing.setImageUrl(ingNode.get("image").asText());

			// 			// Extract quantity from unit (e.g. "600 g" -> 600)
			// 			String unitStr = ingNode.get("unit").asText();
			// 			try {
			// 				Integer quantity = Integer.parseInt(unitStr.split(" ")[0]);
			// 				ing.setUnit(unitStr.split(" ")[1]);
			// 				ing.setQuantity(quantity);
			// 			} catch (Exception e) {
			// 				ing.setQuantity(0); // or handle gracefully
			// 			}

			// 			ingredients.add(ing);
			// 		}

			// 		dish.setIngredients(ingredients);
			// 		dishRepository.save(dish);
			// 	}
			// } catch (Exception e) {
			// 	e.printStackTrace();
			// }

			// jsonFilePath = Paths.get(currentDir, "src", "main", "java", "com", "markendation", "server", "ingredients_analysis.json").toString();
			// try {
			// 	content = new String(Files.readAllBytes(Paths.get(jsonFilePath)), java.nio.charset.StandardCharsets.UTF_8);
			// 	JSONArray ingredientsArray = new JSONArray(content);

			// 	for (int i = 0; i < ingredientsArray.length(); i++) {
			// 		JSONObject item = ingredientsArray.getJSONObject(i);
			// 		for (String key : item.keySet()) {
			// 			JSONArray innerArray = item.getJSONArray(key);
			// 			JSONObject ingredient = innerArray.getJSONObject(0);
	
			// 			String name = ingredient.getString("ingredient_name");
			// 			String vietnamese = ingredient.getString("vietnamese_name");
			// 			String unit = ingredient.getString("unit");
			// 			String category = ingredient.getString("category");
			// 			String image = ingredient.getString("image");
	
			// 			Ingredient newIngredient = Ingredient.builder()
			// 										.name(name)
			// 										.vietnameseName(vietnamese)
			// 										.unit(unit)
			// 										.category(category)
			// 										.imageUrl(image).build();
						
			// 			ingredientRepository.save(newIngredient);
			// 		}
			// 	}
			// } catch (IOException e) {
			// 	System.err.println("Error reading file: " + e.getMessage());
			// }
		};
	}

}
