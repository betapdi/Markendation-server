package com.markendation.server.services;

import org.springframework.stereotype.Service;

import com.markendation.server.auth.entities.User;
import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.dto.BasketDto;
import com.markendation.server.dto.IngredientDto;
import com.markendation.server.exceptions.UserNotFoundException;
import com.markendation.server.models.Ingredient;
import com.markendation.server.repositories.primary.BasketRepository;
import com.markendation.server.repositories.primary.DishRepository;
import com.markendation.server.repositories.primary.IngredientRepository;

@Service
public class BasketService {
    private final BasketRepository basketRepository;
    private final IngredientRepository ingredientRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public BasketService(BasketRepository basketRepository, IngredientRepository ingredientRepository,
            DishRepository dishRepository, UserRepository userRepository) {
        this.basketRepository = basketRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    public BasketDto getBasket(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
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
}
