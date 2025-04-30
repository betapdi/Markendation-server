package com.markendation.server.utils;

import java.util.ArrayList;
import java.util.List;

import com.markendation.server.models.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreCalculation {
    private Store store;
    private List<ProductCost> products = new ArrayList<>();
    private Integer totalCost = 0;
    private double distance;
    private float stars = 0;
    private double rating;
}
