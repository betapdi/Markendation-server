package com.markendation.server.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(
    basePackages = {
        "com.markendation.server.auth.repositories",
        "com.markendation.server.repositories.primary"
    },
    mongoTemplateRef = "primaryMongoTemplate"
)
@Configuration
public class PrimaryMongoConfig {

    @Value("${mongodb.primary.username}")
    private String username;

    @Value("${mongodb.primary.password}")
    private String password;

    @Value("${mongodb.primary.host}")
    private String host;

    @Value("${mongodb.primary.port}")
    private int port;

    @Value("${mongodb.primary.database}")
    private String database;

    @Value("${mongodb.primary.authentication-database}")
    private String authDb;

    @Bean(name = "primaryMongoDatabaseFactory")
    @Primary
    public MongoDatabaseFactory primaryMongoDatabaseFactory() {
        String uri = String.format(
            "mongodb://%s:%s@%s:%d/%s?authSource=%s",
            username, password, host, port, database, authDb
        );
        return new SimpleMongoClientDatabaseFactory(uri);
    }

    @Bean(name = "primaryMongoTemplate")
    @Primary
    public MongoTemplate primaryMongoTemplate() {
        return new MongoTemplate(primaryMongoDatabaseFactory());
    }
}