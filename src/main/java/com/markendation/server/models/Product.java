package com.markendation.server.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    private String id;

    private String sku;

    private String store;
    
    private String store_id;

    private String category;

    private String image;

    private String name;

    private String name_en;

    private Integer price;

    private Integer netUnitValue;

    private String unit;

    private List<String> token_ngrams;

    private String url;
}
