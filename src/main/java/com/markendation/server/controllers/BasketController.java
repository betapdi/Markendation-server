package com.markendation.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.markendation.server.dto.BasketDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.services.BasketService;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/basket")
public class BasketController {
    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("")
    public ResponseEntity<BasketDto> getBasket(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        BasketDto response = basketService.getBasket(userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add/ingredient")
    public ResponseEntity<String> addIngredient(@AuthenticationPrincipal UserDetails userDetails, @RequestBody IngredientDto ingredientDto) throws IOException {
        basketService.addIngredient(userDetails.getUsername(), ingredientDto);
        return new ResponseEntity<>("Ingredient added!", HttpStatus.OK);
    }
       
}
