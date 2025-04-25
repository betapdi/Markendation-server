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
    basePackages = "com.markendation.server.repositories.secondary", 
    mongoTemplateRef = "secondaryMongoTemplate"
)
public class SecondaryMongoConfig {
    @Value("${mongodb.secondary.uri}")
    private String secondaryUri;

    @Bean(name = "secondaryMongoDbFactory")
    public MongoDatabaseFactory secondaryMongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(secondaryUri);
    }

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() {
        return new MongoTemplate(secondaryMongoDbFactory());
    }
}
