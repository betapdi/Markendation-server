package com.markendation.server.kafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class IngredientConsumer {
    @KafkaListener(topics = "ingredient-response-dsh")
    public void consume(String message) {
        System.out.println("Received message from ingredient-dsh: " + message);
    }
}
