package com.markendation.server.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.markendation.server.dto.DishDto;
import com.markendation.server.kafka.IngredientConsumer;
import com.markendation.server.kafka.KafkaProducer;
import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.DishRepository;
import com.markendation.server.repositories.primary.IngredientRepository;
import com.markendation.server.utils.ImageModelEvent;
import com.markendation.server.utils.ModelResponse;
import com.markendation.server.utils.TextModelEvent;

@Service
public class AIService {
    private final S3Service s3Service;
    private final KafkaProducer kafkaProducer;
    private final IngredientConsumer ingredientConsumer;
    private final IngredientRepository ingredientRepository;
    private final DishRepository dishRepository;

    public AIService(S3Service s3Service, com.markendation.server.kafka.KafkaProducer kafkaProducer, 
                        IngredientConsumer ingredientConsumer, IngredientRepository ingredientRepository,
                        DishRepository dishRepository) {
        this.s3Service = s3Service;
        this.kafkaProducer = kafkaProducer;
        this.ingredientConsumer = ingredientConsumer;
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public DishDto extractFromText(String foodDescription) throws InterruptedException, ExecutionException, TimeoutException {
        TextModelEvent event = new TextModelEvent();
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<ModelResponse> future = new CompletableFuture<>();
        
        ingredientConsumer.getFutures().put(correlationId, future);
        event.setCorrelationId(correlationId);
        event.setDescription(foodDescription);

        kafkaProducer.send(event);

        ModelResponse response = future.get(10, TimeUnit.SECONDS);
        Dish dish = response.toDish();

        for (Ingredient ingredient : dish.getIngredients()) {
            ingredient = ingredientRepository.save(ingredient);
        }
        dish = dishRepository.save(dish);
        
        DishDto result = new DishDto();
        result.update(dish);

        return result;
    }

    public DishDto extractFromImage(MultipartFile image) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        String imageUrl = s3Service.uploadImage(image);

        ImageModelEvent event = new ImageModelEvent();
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<ModelResponse> future = new CompletableFuture<>();
        
        ingredientConsumer.getFutures().put(correlationId, future);
        event.setCorrelationId(correlationId);
        event.setImageUrl(imageUrl);

        kafkaProducer.send(event);

        ModelResponse response = future.get(10, TimeUnit.SECONDS);
        Dish dish = response.toDish();

        for (Ingredient ingredient : dish.getIngredients()) {
            ingredient = ingredientRepository.save(ingredient);
        }
        dish = dishRepository.save(dish);
        
        DishDto result = new DishDto();
        result.update(dish);

        return result;
    }
}
