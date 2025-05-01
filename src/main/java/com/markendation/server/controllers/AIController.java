package com.markendation.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.markendation.server.dto.DishDto;
import com.markendation.server.services.AIService;

import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/image")
    public ResponseEntity<DishDto> processImage(@RequestParam("image") MultipartFile file) throws IOException {
        DishDto response = aiService.extractFromImage(file);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/text")
    public ResponseEntity<DishDto> processText(@RequestBody Map<String, String> mapping) {
        String description = mapping.get("description");
        DishDto response = aiService.extractFromText(description);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
}
