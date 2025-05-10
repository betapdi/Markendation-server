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
    private Integer quantity;
    private Integer cost;
    private Integer productIndex = -1;
}
