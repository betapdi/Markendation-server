package com.markendation.server.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.markendation.server.dto.DishDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.kafka.KafkaProducer;

@Service
public class AIService {
    private final S3Service s3Service;
    private final KafkaProducer KafkaProducer;

    public AIService(S3Service s3Service, com.markendation.server.kafka.KafkaProducer kafkaProducer) {
        this.s3Service = s3Service;
        KafkaProducer = kafkaProducer;
    }

    public DishDto extractFromText(String foodDescription) {
        DishDto response = new DishDto();

        return response;
    }

    public DishDto extractFromImage(MultipartFile image) {
        DishDto response = new DishDto();
        
        return response;
    }
}
