package com.markendation.server.services;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.markendation.server.classes.ImageModelEvent;
import com.markendation.server.classes.ModelResponse;
import com.markendation.server.classes.TextModelEvent;
import com.markendation.server.dto.DishDto;
import com.markendation.server.kafka.IngredientConsumer;
import com.markendation.server.kafka.KafkaProducer;
import com.markendation.server.models.Dish;

@Service
public class AIService {
    private final S3Service s3Service;
    private final KafkaProducer kafkaProducer;
    private final IngredientConsumer ingredientConsumer;

    public AIService(S3Service s3Service, KafkaProducer kafkaProducer, 
                        IngredientConsumer ingredientConsumer) {
        this.s3Service = s3Service;
        this.kafkaProducer = kafkaProducer;
        this.ingredientConsumer = ingredientConsumer;
    }

    public DishDto extractFromText(String foodDescription) throws InterruptedException, ExecutionException, TimeoutException {
        TextModelEvent event = new TextModelEvent();
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<ModelResponse> future = new CompletableFuture<>();
        
        ingredientConsumer.getFutures().put(correlationId, future);
        event.setCorrelationId(correlationId);
        event.setRequestMessage(foodDescription);

        kafkaProducer.send(event);

        ModelResponse response = future.get(25, TimeUnit.SECONDS);
        Dish dish = response.getIngredients().getDishes().get(0).toDish();

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
        event.setFileName(imageUrl);

        kafkaProducer.send(event);

        ModelResponse response = future.get(15, TimeUnit.SECONDS);
        Dish dish = response.getIngredients().getDishes().get(0).toDish();

        DishDto result = new DishDto();
        result.update(dish);

        return result;
    }
}
