package com.markendation.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.markendation.server.auth.entities.User;
import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.classes.StoreCalculation;
import com.markendation.server.dto.BasketDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.exceptions.UserNotFoundException;
import com.markendation.server.models.Basket;
import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.BasketRepository;

@Service
public class BasketService {
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final CalculatingService calculatingService;
    private final UserService userService;

    public BasketService(BasketRepository basketRepository, UserRepository userRepository, CalculatingService calculatingService,
            UserService userService) {
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
        this.calculatingService = calculatingService;
        this.userService = userService;
    }

    public BasketDto getBasket(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        BasketDto response = new BasketDto();
        response.update(user.getBasket());

        return response;
    }

    public BasketDto updateBasket(String email, BasketDto dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        user.getBasket().update(dto);
        basketRepository.save(user.getBasket());    
        userRepository.save(user);

        BasketDto response = new BasketDto();
        response.update(user.getBasket());
        
        return response;
    }

    public void addIngredient(String email, IngredientDto dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        for (Ingredient ingredient : user.getBasket().getIngredients()) {
            if (ingredient.getId().equals(dto.getId())) {
                float quantity = ingredient.getQuantity() + dto.getQuantity();
                ingredient.setQuantity(quantity);
                userRepository.save(user);
                return;
            }
        }

        Ingredient ingredient = new Ingredient();
        ingredient.update(dto);

        user.getBasket().getIngredients().add(ingredient);
        userRepository.save(user);
    }

    public void addDish(String email, IngredientDto dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        for (Ingredient ingredient : user.getBasket().getIngredients()) {
            if (ingredient.getId().equals(dto.getId())) {
                float quantity = ingredient.getQuantity() + dto.getQuantity();
                ingredient.setQuantity(quantity);
                userRepository.save(user);
                return;
            }
        }

        Ingredient ingredient = new Ingredient();
        ingredient.update(dto);

        user.getBasket().getIngredients().add(ingredient);
        userRepository.save(user);
    }

    public List<StoreCalculation> recommendStore(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());

        if (user.getLocation().getLatitude() != 0 && (user.getNearStores() == null || user.getNearStores().isEmpty())) {
            userService.updateNearStore(user);
        }

        Map<String, Ingredient> ingredientMap = new TreeMap<>();
        
        for (Ingredient ingredient : user.getBasket().getIngredients()) {
            if (ingredient.getId() != null) {
                ingredientMap.put(ingredient.getId(), ingredient);
            }

            else {
                String uuid = UUID.randomUUID().toString();
                ingredient.setId(uuid);
                ingredientMap.put(uuid, ingredient);
            }
        }

        for (Dish dish : user.getBasket().getDishes()) {
            for (Ingredient ingredient : dish.getIngredients()) {
                String id = ingredient.getId();
                if (ingredientMap.containsKey(id)) {
                    Ingredient chosen = ingredientMap.get(id);
                    float quantity = chosen.getQuantity() + ingredient.getQuantity();
                    chosen.setQuantity(quantity);

                    ingredientMap.put(id, chosen);
                }

                else if (ingredient.getId() != null) {
                    ingredientMap.put(ingredient.getId(), ingredient);
                }

                else {
                    String uuid = UUID.randomUUID().toString();
                    ingredient.setId(uuid);
                    ingredientMap.put(uuid, ingredient);
                }
            }
        }

        List<Ingredient> ingredients = new ArrayList<>(ingredientMap.values());
        List<StoreCalculation> response = calculatingService.calculateIngredients(user, ingredients);
        return response;
    }

    public String saveBasket(String email, BasketDto dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        Basket basket = new Basket();
        basket.update(dto);

        System.out.println(basket);

        user.getSavedBaskets().add(basket);
        userRepository.save(user);

        return "Basket saved!";
    }

    public String removeSavedBasket(String email, int index) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        user.getSavedBaskets().remove(index);

        userRepository.save(user);
        return "Basket removed!";
    }

    public List<BasketDto> getSavedBaskets(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        
        List<BasketDto> response = new ArrayList<>();
        for (Basket basket : user.getSavedBaskets()) {
            BasketDto dto = new BasketDto();
            dto.update(basket);

            response.add(dto);
        }

        return response;
    }
}
