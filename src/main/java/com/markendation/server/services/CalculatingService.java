package com.markendation.server.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Service
public class CalculatingService {
    private static MetaCategoryRepository metaCategoryRepository;
    private final StoreRepository storeRepository;

    @Value("${openRoute.api.key}")
    private static String apiKey;

    private static Map<String, MongoTemplate> shardTemplateCache = new ConcurrentHashMap<>();
    private LRUCache<Pair<String, String>, Product> productCache = new LRUCache<>(1000);

    public CalculatingService(MetaCategoryRepository myMetaCategoryRepository, StoreRepository storeRepository) {
        metaCategoryRepository = myMetaCategoryRepository;
        this.storeRepository = storeRepository;
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

        Client client = ClientBuilder.newClient();
        Response response = client.target(urlString)
        .request(MediaType.TEXT_PLAIN_TYPE)
        .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
        .get();

        // System.out.println("status: " + response.getStatus());
        // System.out.println("headers: " + response.getHeaders());
        // System.out.println("body:" + response.readEntity(String.class));

        try {
            String json = response.readEntity(String.class);
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(json);

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token) && "distance".equals(parser.currentName())) {
                    parser.nextToken();
                    return parser.getDoubleValue() / 1000;
                }
            }

            //distance not found
            return -1;
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
            System.out.println(product);
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

    public Product findBestProduct(String storeId, Ingredient ingredient) {
        Pair<String, String> cachedKey = Pair.of(storeId, ingredient.getId());
        if (productCache.containsKey(cachedKey)) return productCache.get(cachedKey);

        List<Product> matchedProducts = findProductsByStoreIdAndIngredient(storeId, ingredient);
        Product chosen = new Product(); float minCost = Float.MAX_VALUE;
        chosen.setPrice(-1);

        for (Product dirtyProduct : matchedProducts) {
            int unit = dirtyProduct.getNetUnitValue();

            if (minCost > (dirtyProduct.getPrice() / unit)) {
                minCost = dirtyProduct.getPrice() / unit;
                chosen = dirtyProduct;
            }
        }

        productCache.put(cachedKey, chosen);
        return chosen;
    }

    public List<StoreCalculation> calculateTotalCost(List<Pair<String, Double>> nearStores, List<Ingredient> ingredients) {
        List<StoreCalculation> result = new ArrayList<>();
        for (Pair<String, Double> storeIndex : nearStores) {
            Store store = storeRepository.findById(storeIndex.getFirst()).orElseThrow(() -> new StoreNotFoundException());
            Boolean flag = true;

            StoreCalculation storeData = new StoreCalculation();
            storeData.setStore(store); storeData.setDistance(storeIndex.getSecond());

            for (Ingredient ingredient : ingredients) {
                Product bestProduct = findBestProduct(store.getId(), ingredient);
                if (bestProduct.getPrice() == -1) flag = false;

                int quantity = getQuantityNeeded(ingredient, bestProduct);  
                if (quantity == -1) flag = false;

                if (!flag) break;

                ProductCost productData = new ProductCost(bestProduct, quantity, bestProduct.getPrice() * quantity);
                storeData.getProducts().add(productData);
                storeData.setTotalCost(storeData.getTotalCost() + productData.getCost());
            }

            if (flag) result.add(storeData);
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

        for (StoreCalculation storeData : storeDatas) {
            double costRating = (maxCost == minCost ? (5 * (maxCost - storeData.getTotalCost()) / (maxCost - minCost)) : 5);
            double distRating = (maxDist == minDist ? (5 * (maxDist - storeData.getDistance()) / (maxDist - minDist)) : 5);
            // System.out.println(costRating); System.out.println(distRating); 
            double rating = costRating * 0.0883 + storeData.getStars() * 0.4824 + distRating * 0.1575 + storeData.getRecently() * 0.2718;
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
