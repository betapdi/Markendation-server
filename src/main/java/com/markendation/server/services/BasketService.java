package com.markendation.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.markendation.server.auth.entities.User;
import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.dto.BasketDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.exceptions.UserNotFoundException;
import com.markendation.server.models.Basket;
import com.markendation.server.models.Dish;
import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.BasketRepository;
import com.markendation.server.repositories.primary.DishRepository;
import com.markendation.server.repositories.primary.IngredientRepository;
import com.markendation.server.utils.StoreCalculation;

@Service
public class BasketService {
    private final BasketRepository basketRepository;
    private final IngredientRepository ingredientRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final CalculatingService calculatingService;

    public BasketService(BasketRepository basketRepository, IngredientRepository ingredientRepository,
            DishRepository dishRepository, UserRepository userRepository, CalculatingService calculatingService) {
        this.basketRepository = basketRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.calculatingService = calculatingService;
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
        Map<String, Ingredient> ingredientMap = new TreeMap<>();
        
        for (Ingredient ingredient : user.getBasket().getIngredients()) {
            ingredientMap.put(ingredient.getId(), ingredient);
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

                else ingredientMap.put(ingredient.getId(), ingredient);
            }
        }

        List<Ingredient> ingredients = new ArrayList<>(ingredientMap.values());
        List<StoreCalculation> response = calculatingService.calculateIngredients(user.getLocation(), ingredients);
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
}
