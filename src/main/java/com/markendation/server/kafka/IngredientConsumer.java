package com.markendation.server.kafka;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.markendation.server.utils.ModelResponse;

import lombok.Getter;

@Service
@Getter
public class IngredientConsumer {
    private final Map<String, CompletableFuture<ModelResponse>> futures = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${kafka.consumer.topic}")
    public void consume(ModelResponse message) {
        System.out.println("Received message from ingredient-dsh: " + message);

        String correlationId = message.getCorrelationId();
        CompletableFuture<ModelResponse> future = futures.remove(correlationId);
        if (future != null) {
            future.complete(message);
        }
    }
}
