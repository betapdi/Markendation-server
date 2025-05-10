package com.markendation.server.classes;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.dto.DishDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageDishResponse {
    private Integer numDishes;
    private List<DishDto> dishes = new ArrayList<>();
}
