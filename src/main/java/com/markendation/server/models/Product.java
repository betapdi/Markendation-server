package com.markendation.server.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Document(collection = "product_prices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    private String id;

    private String sku;

    private String store;

    private String category;

    private String date_begin;

    private String date_end;

    private float discount;

    private String excerpt;

    private String image;

    private String link;

    private String name;

    private String name_ev;

    private Integer price;

    private String promotion;

    private String store_id;

    private LocalDateTime ts;

    private String unit;
}
