package com.markendation.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

			// // Construct the path to the JSON file manually
			// String jsonFilePath = Paths.get(currentDir, "src", "main", "java", "com", "markendation", "server", "ingredients_cleaned.json").toString();

			// ObjectMapper mapper = new ObjectMapper();
			// List<IngredientCleaned> ingredients = mapper.readValue(new File(jsonFilePath), new TypeReference<List<IngredientCleaned>>() {});

			// for (IngredientCleaned ingredient : ingredients) {
			// 	Ingredient newIngredient = new Ingredient();
			// 	newIngredient.setName(ingredient.getName());
			// 	newIngredient.setCategory(ingredient.getCategory());
			// 	newIngredient.setImageUrl(ingredient.getImage());
			// 	newIngredient.setVietnameseName(ingredient.getVietnamese_name());
			// 	newIngredient.setUnit(ingredient.getUnit());
			// 	ingredientRepository.save(newIngredient);
			// }
		};
	}

}
