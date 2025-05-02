package com.markendation.server.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.markendation.server.exceptions.CategoryNotFoundException;
import com.markendation.server.models.MetaCategory;
import com.markendation.server.models.Ingredient;
import com.markendation.server.models.Product;
import com.markendation.server.models.Store;
import com.markendation.server.repositories.metadata.MetaCategoryRepository;
import com.markendation.server.repositories.metadata.StoreRepository;
import com.markendation.server.utils.KMPProductMatcher;
import com.markendation.server.utils.LRUCache;
import com.markendation.server.utils.Location;
import com.markendation.server.utils.ProductCost;
import com.markendation.server.utils.StoreCalculation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Service
public class CalculatingService {
    private final MetaCategoryRepository metaCategoryRepository;
    private final StoreRepository storeRepository;

    @Value("${openRoute.api.key}")
    String apiKey;

    private Map<String, MongoTemplate> shardTemplateCache = new ConcurrentHashMap<>();
    private LRUCache<Pair<String, String>, Product> productCache = new LRUCache<>(1000);

    public CalculatingService(MetaCategoryRepository metaCategoryRepository, StoreRepository storeRepository) {
        this.metaCategoryRepository = metaCategoryRepository;
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

    private MongoTemplate getShardTemplate(String collectionName, String serverUri, String dbName) {
        String key = collectionName;
        return shardTemplateCache.computeIfAbsent(key, k -> {
            MongoClient mongoClient = MongoClients.create(serverUri);
            return new MongoTemplate(mongoClient, dbName);
        });
    }

    public Pair<MongoTemplate, String> upsertCategoryShard(String categoryEng) {
        MetaCategory categoryMetadata = metaCategoryRepository.findByCategory(categoryEng).orElseThrow(() -> new CategoryNotFoundException());

        String dbName = categoryMetadata.getDb_name();
        String collectionName = categoryMetadata.getCollection_name();
        String serverUri = categoryMetadata.getServer_uri();
        
        return Pair.of(getShardTemplate(collectionName, serverUri, dbName), collectionName);
    }

    public int getUnit(Product product) {
        if (product.getUnit() == null) return -1;
        else if (product.getUnit().equals("kg")) return 1000;
        else {
            String productUnit = product.getName_ev().substring(product.getName_ev().lastIndexOf(' ') + 1);
            Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z]+)");
            Matcher matcher = pattern.matcher(productUnit);

            if (matcher.matches()) {
                int number = Integer.parseInt(matcher.group(1)); 
                String unit = matcher.group(2);  
                
                if (unit.equals("kg") || unit.equals("l")) {
                    number *= 1000;
                }

                else if (!unit.equals("ml") && !unit.equals("g")) return -1;

                return number;
            }

            return -1;
        }
    }
    
    public int getQuantityNeeded(Ingredient ingredient, Product product) {
        int unit = getUnit(product), addition = 0;
        if (unit == -1) return -1;

        if (ingredient.getQuantity() % unit != 0) addition = 1;
        return (int)(ingredient.getQuantity() / unit) + addition;
    }

    public Product findBestProduct(String storeId, Ingredient ingredient) {
        Pair<String, String> cachedKey = Pair.of(storeId, ingredient.getId());
        if (productCache.containsKey(cachedKey)) return productCache.get(cachedKey);

        Pair<MongoTemplate, String> collectionTemplate = upsertCategoryShard(ingredient.getCategory());
        Query query = new Query();

        query.addCriteria(Criteria.where("store").is(storeId));
        List<Product> dirtyProducts = collectionTemplate.getFirst().
                                        find(query, Product.class, collectionTemplate.getSecond());

        KMPProductMatcher matcher = new KMPProductMatcher(ingredient.getName());
        Product chosen = new Product(); float minCost = Float.MAX_VALUE;
        chosen.setPrice(-1);

        for (Product dirtyProduct : dirtyProducts) {
            if (!dirtyProduct.getStore_id().equals(storeId)) continue;

            if (matcher.isMatch(dirtyProduct.getName_ev())) {
                int unit = getUnit(dirtyProduct);
                if (unit == -1) continue;

                if (minCost > (dirtyProduct.getPrice() / unit)) {
                    minCost = dirtyProduct.getPrice() / unit;
                    chosen = dirtyProduct;
                }
            }
        }

        productCache.put(cachedKey, chosen);
        return chosen;
    }

    public List<StoreCalculation> calculateTotalCost(List<Store> stores, List<Ingredient> ingredients) {
        List<StoreCalculation> result = new ArrayList<>();
        for (Store store : stores) {
            Boolean flag = true;
            StoreCalculation storeData = new StoreCalculation();
            storeData.setStore(store);

            for (Ingredient ingredient : ingredients) {
                Product bestProduct = findBestProduct(store.getStore_id(), ingredient);
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

    public List<StoreCalculation> calculateRating(List<Store> stores, List<Ingredient> ingredients, Location userLocation) {
        List<StoreCalculation> storeDatas = calculateTotalCost(stores, ingredients);

        double maxCost = -1, minCost = Double.MAX_VALUE, maxDist = -1, minDist = Double.MAX_VALUE;
        for (StoreCalculation storeData : storeDatas) {
            Store store = storeData.getStore();
            double lon1 = userLocation.getLongitude(), lat1 = userLocation.getLatitude();
            double lon2 = Double.parseDouble(store.getLocation().getCoordinates().get(0));
            double lat2 = Double.parseDouble(store.getLocation().getCoordinates().get(1));
            
            double distance = getRouteDistance(lon1, lat1, lon2, lat2); storeData.setDistance(distance);
            maxDist = Math.max(maxDist, storeData.getDistance());
            minDist = Math.min(minDist, storeData.getDistance());
            
            maxCost = Math.max(maxCost, storeData.getTotalCost());
            minCost = Math.min(minCost, storeData.getTotalCost());
        }

        for (StoreCalculation storeData : storeDatas) {
            double costRating = 5 * (maxCost - storeData.getTotalCost()) / (maxCost - minCost);
            double distRating = 5 * (maxDist - storeData.getDistance()) / (maxDist - minDist);
            // System.out.println(costRating); System.out.println(distRating); 
            double rating = costRating * 0.0883 + storeData.getStars() * 0.4824 + distRating * 0.1575;
            storeData.setRating(rating);
        }

        return storeDatas;
    }

    public double getRouteDistance(double lon1, double lat1, double lon2, double lat2) {
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

            throw new IllegalStateException("Distance not found in JSON");
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return 11;
    }

    public List<StoreCalculation> calculateIngredients(Location userLocation, List<Ingredient> ingredients) {
        List<Store> dirtyStores = storeRepository.findAll();
        List<Store> cleanedStores = new ArrayList<>();
        Map<String, Integer> cntChain = new HashMap<>();
        double radius = 10;

        for (Store store : dirtyStores) {
            String chain = store.getChain();
            int num = (cntChain.get(chain) != null) ? cntChain.get(chain) : 0;
            if (num == 5) continue;

            double lon1 = userLocation.getLongitude(), lat1 = userLocation.getLatitude();
            double lon2 = Double.parseDouble(store.getLocation().getCoordinates().get(0));
            double lat2 = Double.parseDouble(store.getLocation().getCoordinates().get(1));

            if (calculateDistance(lat1, lon1, lat2, lon2) <= radius) {
                cntChain.put(store.getChain(), num + 1);
                cleanedStores.add(store);
            }
        }

        List<StoreCalculation> storeDatas = calculateRating(cleanedStores, ingredients, userLocation);

        return storeDatas;
    }
}
