package com.markendation.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.markendation.server.classes.LocationMeta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Document(collection = "store_branches")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Store {
    @Id
    private String id;

    private String chain;

    private String store_id;

    private String address;

    private String city;

    private String district;

    private LocationMeta location;

    private String name;

    private String ward;

    private String phone;
}
