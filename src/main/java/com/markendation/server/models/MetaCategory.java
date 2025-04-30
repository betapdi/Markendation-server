package com.markendation.server.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Document(collection = "category_shards")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MetaCategory {
    @Id
    private String id;

    @Field("Category")
    private String category;

    private String db_name;

    private String collection_name;

    private String server_uri;
}
