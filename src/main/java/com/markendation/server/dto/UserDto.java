package com.markendation.server.dto;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.auth.entities.User;
import com.markendation.server.classes.Location;
import com.markendation.server.models.Basket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String id;
    private String fullname;
    private String email;
    private BasketDto basket;
    private Location location;
    private List<BasketDto> savedBaskets;

    public void update(User user) {
        id = user.getId();
        fullname = user.getFullname();
        email = user.getEmail();
        location = user.getLocation();

        basket = new BasketDto();
        basket.update(user.getBasket());

        savedBaskets = new ArrayList<>();
        for (Basket basket : user.getSavedBaskets()) {
            BasketDto dto = new BasketDto();
            dto.update(basket);
            savedBaskets.add(dto);
        }
    }
}
