package com.markendation.server.services;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.markendation.server.dto.DishDto;
import com.markendation.server.kafka.KafkaProducer;
import com.markendation.server.utils.ImageModelEvent;
import com.markendation.server.utils.TextModelEvent;

@Service
public class AIService {
    private final S3Service s3Service;
    private final KafkaProducer kafkaProducer;

    public AIService(S3Service s3Service, com.markendation.server.kafka.KafkaProducer kafkaProducer) {
        this.s3Service = s3Service;
        this.kafkaProducer = kafkaProducer;
    }

    public DishDto extractFromText(String foodDescription) {
        TextModelEvent event = new TextModelEvent();
        event.setDescription(foodDescription);
        kafkaProducer.send(event);
        DishDto response = new DishDto();

        return response;
    }

    public DishDto extractFromImage(MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadImage(image);
        ImageModelEvent event = new ImageModelEvent();
        event.setImageUrl(imageUrl);
        kafkaProducer.send(event);
        DishDto response = new DishDto();
        
        return response;
    }
}
