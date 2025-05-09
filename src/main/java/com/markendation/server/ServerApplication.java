package com.markendation.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			// String categoryEng = "Snacks";
			// MetaCategory categoryMetadata = metaCategoryRepository.findByCategory(categoryEng).orElseThrow(() -> new CategoryNotFoundException());

			// String dbName = categoryMetadata.getDb_name();
			// String collectionName = categoryMetadata.getCollection_name();
			// String serverUri = categoryMetadata.getServer_uri();

			// MongoClient mongoClient = MongoClients.create(serverUri);
            // MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, dbName);

			// Index index = new Index()	
			// 	.on("store_id", Direction.ASC)
			// 	.on("nameTokenNGrams", Direction.ASC);

			// mongoTemplate.indexOps(collectionName)
            // .ensureIndex(index);

			// List<Product> products = mongoTemplate.findAll(Product.class, collectionName);
			// int cnt = 0;
			// for (Product product : products) {
			// 	++cnt;
			// 	product.setNameTokenNGrams(TokenUtils.generateTokenNGrams(product.getName_ev(), 2));
			// 	mongoTemplate.save(product, collectionName);

			// 	if (cnt % 500 == 0) System.out.println(cnt);
			// }
			
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
			// 				float quantity = Float.parseFloat(unitStr.split(" ")[0]);
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
