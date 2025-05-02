package com.markendation.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.models.Ingredient;
import com.markendation.server.models.Product;
import com.markendation.server.models.Store;
import com.markendation.server.repositories.metadata.StoreRepository;
import com.markendation.server.repositories.primary.IngredientRepository;
import com.markendation.server.services.CalculatingService;
import com.markendation.server.utils.IngredientCleaned;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StoreRepository storeRepository, IngredientRepository ingredientRepository) {
		return args -> {
			// List<Store> stores = storeRepository.findAll();
			// int cnt = 0;
			// for (Store store : stores) {
			// 	if (Double.parseDouble(store.getLocation().getCoordinates().get(0)) == 0) {
			// 		System.out.println(store.getId());
			// 	}
			// }

			// System.out.println(cnt);
			// String currentDir = System.getProperty("user.dir");

			// String jsonFilePath = Paths.get(currentDir, "src", "main", "java", "com", "markendation", "server", "ingredients_analysis.json").toString();

			// try {
			// 	String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)), java.nio.charset.StandardCharsets.UTF_8);
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
