package com.markendation.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.markendation.server.dto.BasketDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.services.BasketService;
import com.markendation.server.utils.StoreCalculation;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



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

    @PostMapping("/update")
    public ResponseEntity<BasketDto> updateBasket(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BasketDto basket) throws IOException {
        BasketDto response = basketService.updateBasket(userDetails.getUsername(), basket);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/calculate")
    public ResponseEntity<List<StoreCalculation>> calculateResult(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        List<StoreCalculation> response = basketService.recommendStore(userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/savedBaskets")
    public ResponseEntity<List<BasketDto>> getMethodName(@AuthenticationPrincipal UserDetails userDetails) {
        List<BasketDto> response = basketService.getSavedBaskets(userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    @PostMapping("/save")
    public ResponseEntity<String> saveBasket(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BasketDto basket) {
        String response = basketService.saveBasket(userDetails.getUsername(), basket);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/remove/{index}")
    public ResponseEntity<String> removeSavedBasket(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("index") int index) {
        String response = basketService.removeSavedBasket(userDetails.getUsername(), index);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
}
