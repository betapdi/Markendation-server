package com.markendation.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "com.markendation.server.repositories.metadata", 
    mongoTemplateRef = "metadataMongoTemplate"
)
public class MetadataMongoConfig {
    @Value("${mongodb.metadata.uri}")
    private String metadataUri;

    @Bean(name = "metadataMongoDbFactory")
    public MongoDatabaseFactory metadataMongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(metadataUri);
    }

    @Bean(name = "metadataMongoTemplate")
    public MongoTemplate metadataMongoTemplate() {
        return new MongoTemplate(metadataMongoDbFactory());
    }
}
