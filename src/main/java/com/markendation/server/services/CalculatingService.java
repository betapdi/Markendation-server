package com.markendation.server.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.markendation.server.auth.entities.User;
import com.markendation.server.classes.ProductCost;
import com.markendation.server.classes.StoreCalculation;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.exceptions.CategoryNotFoundException;
import com.markendation.server.exceptions.StoreNotFoundException;
import com.markendation.server.models.MetaCategory;
import com.markendation.server.models.Ingredient;
import com.markendation.server.models.Product;
import com.markendation.server.models.Store;
import com.markendation.server.repositories.metadata.MetaCategoryRepository;
import com.markendation.server.repositories.metadata.StoreRepository;
import com.markendation.server.utils.LRUCache;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Service
public class CalculatingService {
    private static MetaCategoryRepository metaCategoryRepository;
    private final StoreRepository storeRepository;

    @Value("${openRoute.api.key}")
    private String tempKey;

    private static String apiKey;

    private static Map<String, MongoTemplate> shardTemplateCache = new ConcurrentHashMap<>();
    private LRUCache<Pair<String, String>, List<Product>> productCache = new LRUCache<>(1000);

    public CalculatingService(MetaCategoryRepository myMetaCategoryRepository, StoreRepository storeRepository) {
        metaCategoryRepository = myMetaCategoryRepository;
        this.storeRepository = storeRepository;
    }

    @PostConstruct
    public void init() {
        apiKey = tempKey;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in kilometers
        final double R = 6371.0;
        
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Differences in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Distance in kilometers
        double distance = R * c;
        
        return distance;
    }

    public static double getRouteDistance(double lon1, double lat1, double lon2, double lat2) {
        String urlString = String.format(
        "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%f,%f&end=%f,%f",
        apiKey, lon1, lat1, lon2, lat2);
        // System.out.println(urlString);

        Client client = ClientBuilder.newClient();
        Response response = client.target(urlString)
        .request(MediaType.TEXT_PLAIN_TYPE)
        .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
        .get();

        // System.out.println(String.format("%f, %f, %f, %f", lon1, lat1, lon2, lat2));

        try {
            String json = response.readEntity(String.class);
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(json);

            Double distance = (double)-1;

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token) && "distance".equals(parser.currentName())) {
                    parser.nextToken();
                    if (distance == -1) {
                        distance = parser.getDoubleValue() / 1000;
                        parser.close();
                    }
                }
            }

            // System.out.println(distance);
            return distance;
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return -1;
    }

    private static MongoTemplate getShardTemplate(String collectionName, String serverUri, String dbName) {
        String key = collectionName;
        return shardTemplateCache.computeIfAbsent(key, k -> {
            MongoClient mongoClient = MongoClients.create(serverUri);
            return new MongoTemplate(mongoClient, dbName);
        });
    }

    public static Pair<MongoTemplate, String> upsertCategoryShard(String categoryEng) {
        MetaCategory categoryMetadata = metaCategoryRepository.findByCategory(categoryEng).orElseThrow(() -> new CategoryNotFoundException());

        String dbName = categoryMetadata.getDb_name();
        String collectionName = categoryMetadata.getCollection_name();
        String serverUri = categoryMetadata.getServer_uri();
        
        return Pair.of(getShardTemplate(collectionName, serverUri, dbName), collectionName);
    }
    
    public int getQuantityNeeded(Ingredient ingredient, Product product) {
        if (product.getNetUnitValue() == null) {
            // System.out.println(product);
            return 1;
        }
        int unit = product.getNetUnitValue(), addition = 0;
        if (unit == -1) return -1;

        if (ingredient.getQuantity() % unit != 0) addition = 1;
        return (int)(ingredient.getQuantity() / unit) + addition;
    }

    public List<Product> findProductsByStoreIdAndIngredient(String storeId, Ingredient ingredient) {
        Pair<MongoTemplate, String> collectionTemplate = upsertCategoryShard(ingredient.getCategory());
        return SearchingService.searchProductsByPatternAndStoreId(collectionTemplate, storeId, ingredient.getName());
    }

    public static Product findProductByIngredient(Ingredient ingredient) {
        Pair<MongoTemplate, String> collectionTemplate = upsertCategoryShard(ingredient.getCategory());
        return SearchingService.searchProductByPattern(collectionTemplate, ingredient.getName());
    }

    public List<Product> findBestProducts(String storeId, Ingredient ingredient) {
        Pair<String, String> cachedKey = Pair.of(storeId, ingredient.getId());
        if (productCache.containsKey(cachedKey)) return productCache.get(cachedKey);

        List<Product> matchedProducts = findProductsByStoreIdAndIngredient(storeId, ingredient);
        List<Product> result = matchedProducts.stream()
                                .filter(product -> product.getNetUnitValue() > 0)
                                .sorted(Comparator.comparingDouble(product -> product.getPrice() / product.getNetUnitValue()))
                                .limit(4)
                                .collect(Collectors.toList());
        productCache.put(cachedKey, result);
        return result;
    }

    public List<StoreCalculation> calculateTotalCost(List<Pair<String, Double>> nearStores, List<Ingredient> ingredients) {
        List<StoreCalculation> result = new ArrayList<>();
        for (Pair<String, Double> storeIndex : nearStores) {
            Store store = storeRepository.findById(storeIndex.getFirst()).orElseThrow(() -> new StoreNotFoundException());

            StoreCalculation storeData = new StoreCalculation();
            storeData.setStore(store); storeData.setDistance(storeIndex.getSecond());
            storeData.setLackIngredients(new ArrayList<>());

            int cnt = 0;
            for (Ingredient ingredient : ingredients) {
                List<Product> bestProducts = findBestProducts(store.getId(), ingredient);
                if (bestProducts.isEmpty()) {
                    IngredientDto dto = new IngredientDto();
                    dto.update(ingredient);
                    storeData.getLackIngredients().add(dto);
                    continue;
                }

                Boolean flag = false;
                for (Product product : bestProducts) {
                    int quantity = getQuantityNeeded(ingredient, product);
                    ProductCost productData = new ProductCost(product, quantity, product.getPrice() * quantity, cnt);
                    
                    if (!flag) {
                        storeData.getProducts().add(productData); 
                        storeData.setTotalCost(storeData.getTotalCost() + productData.getCost());
                        flag = true;
                    }

                    else storeData.getSimilarProducts().add(productData);
                }

                ++cnt;
            }

            if (storeData.getProducts().size() != 0) result.add(storeData);
        }

        return result;
    }

    public List<StoreCalculation> calculateRating(List<Pair<String, Double>> nearStores, List<Ingredient> ingredients) {
        List<StoreCalculation> storeDatas = calculateTotalCost(nearStores, ingredients);

        double maxCost = -1, minCost = Double.MAX_VALUE, maxDist = -1, minDist = Double.MAX_VALUE;
        for (StoreCalculation storeData : storeDatas) {
            maxDist = Math.max(maxDist, storeData.getDistance());
            minDist = Math.min(minDist, storeData.getDistance());
            
            maxCost = Math.max(maxCost, storeData.getTotalCost());
            minCost = Math.min(minCost, storeData.getTotalCost());
        }

        int numIngredients = ingredients.size();

        for (StoreCalculation storeData : storeDatas) {
            double costRating = (maxCost == minCost ? (5 * (maxCost - storeData.getTotalCost()) / (maxCost - minCost)) : 5);
            double distRating = (maxDist == minDist ? (5 * (maxDist - storeData.getDistance()) / (maxDist - minDist)) : 5);

            double cntProductsRating = (double)storeData.getProducts().size() / (double)numIngredients;
            System.out.println(costRating); System.out.println(distRating); System.out.println(distRating); 
            double rating = costRating * 0.244 + cntProductsRating * 0.262 + storeData.getStars() * 0.262 + distRating * 0.091 + storeData.getRecently() * 0.14;
            storeData.setRating(rating);
        }

        return storeDatas;
    }

    public List<StoreCalculation> calculateIngredients(User user, List<Ingredient> ingredients) {
        List<Pair<String, Double>> nearStores = user.getNearStores();
        List<StoreCalculation> storeDatas = calculateRating(nearStores, ingredients);

        return storeDatas;
    }
}
