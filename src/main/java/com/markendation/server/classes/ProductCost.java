package com.markendation.server.classes;

import com.markendation.server.models.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductCost {
    private Product product;
    private Double quantity;
    private Double cost = Double.MAX_VALUE;
    private Integer productIndex = -1;
}
