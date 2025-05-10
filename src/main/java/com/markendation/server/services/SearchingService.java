package com.markendation.server.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.markendation.server.models.Ingredient;
import com.markendation.server.models.Product;
import com.markendation.server.utils.TokenUtils;

@Service
public class SearchingService {
    private static MongoTemplate mongoTemplate;

    public SearchingService(MongoTemplate myMongoTemplate) {
        mongoTemplate = myMongoTemplate;
    }

    public static List<Product> searchProductsByPatternAndStoreId(Pair<MongoTemplate, String> collectionTemplate, String storeId, String pattern) {
        List<String> patternTokens = TokenUtils.tokenizeByWhitespace(pattern);
        List<List<String>> patternTokenNGrams = patternTokens.stream()
            .map(token -> TokenUtils.generateNGrams(token.toLowerCase(), 2))
            .filter(ngrams -> !ngrams.isEmpty())
            .collect(Collectors.toList());

        List<String> patternNGrams = patternTokenNGrams.stream().flatMap(List::stream).collect(Collectors.toList());
        // System.out.println(storeId);
        ObjectId store_id = new ObjectId(storeId);

        Aggregation aggregation = Aggregation.newAggregation(
            //Filter the candidate
            Aggregation.match(
                Criteria.where("store_id").is(store_id)
                    .and("token_ngrams").in(patternNGrams)
            ),

            //Calculate number of ngrams overlaps
            Aggregation.addFields().addFieldWithValue("matchScores",
                patternTokenNGrams.stream()
                    .map(ngrams -> new Document("$sum",
                        ngrams.stream()
                            .map(ngram -> new Document("$cond",
                                Arrays.asList(
                                    new Document("$in", Arrays.asList(ngram, "$token_ngrams")),
                                    1,
                                    0
                                )
                            ))
                            .collect(Collectors.toList())   
                    ))
                    .collect(Collectors.toList())
            ).build(),

            //Get the product with the scores > 0.7
            Aggregation.match(new Criteria().andOperator(
                patternTokenNGrams.stream()
                    .map(ngrams -> Criteria.where("matchScores." + patternTokenNGrams.indexOf(ngrams))
                        .gte((double) Math.ceil(ngrams.size() * 0.8)))
                    .collect(Collectors.toList())
                    .toArray(new Criteria[0])
            ))
        );

        String collectionName = collectionTemplate.getSecond();
        AggregationResults<Product> products = collectionTemplate.getFirst().aggregate(aggregation, collectionName, Product.class);
        
        return products.getMappedResults();
    }

    public static Product searchProductByPattern(Pair<MongoTemplate, String> collectionTemplate, String pattern) {
        List<String> patternTokens = TokenUtils.tokenizeByWhitespace(pattern);
        List<List<String>> patternTokenNGrams = patternTokens.stream()
            .map(token -> TokenUtils.generateNGrams(token.toLowerCase(), 2))
            .filter(ngrams -> !ngrams.isEmpty())
            .collect(Collectors.toList());

        List<String> patternNGrams = patternTokenNGrams.stream().flatMap(List::stream).collect(Collectors.toList());

        Aggregation aggregation = Aggregation.newAggregation(
            //Filter the candidate
            Aggregation.match(
                Criteria.where("token_ngrams").in(patternNGrams)
            ),

            //Calculate number of ngrams overlaps
            Aggregation.addFields().addFieldWithValue("matchScores",
                patternTokenNGrams.stream()
                    .map(ngrams -> new Document("$sum",
                        ngrams.stream()
                            .map(ngram -> new Document("$cond",
                                Arrays.asList(
                                    new Document("$in", Arrays.asList(ngram, "$token_ngrams")),
                                    1,
                                    0
                                )
                            ))
                            .collect(Collectors.toList())   
                    ))
                    .collect(Collectors.toList())
            ).build(),

            //Get the product with the scores > 0.9
            Aggregation.match(new Criteria().andOperator(
                patternTokenNGrams.stream()
                    .map(ngrams -> Criteria.where("matchScores." + patternTokenNGrams.indexOf(ngrams))
                        .gte((double) Math.ceil(ngrams.size() * 0.9)))
                    .collect(Collectors.toList())
                    .toArray(new Criteria[0])
            )),

            Aggregation.limit(1)
        );

        String collectionName = collectionTemplate.getSecond();
        AggregationResults<Product> products = collectionTemplate.getFirst().aggregate(aggregation, collectionName, Product.class);
        List<Product> result = products.getMappedResults();

        if (result.size() > 0) return result.get(0);
        return new Product();
    }

    public static Ingredient searchIngredientByPattern(String pattern) {
        String collectionName = "ingredient";
        Index index = new Index().on("token_ngrams", Direction.ASC);
        mongoTemplate.indexOps(collectionName).ensureIndex(index);

        List<String> patternTokens = TokenUtils.tokenizeByWhitespace(pattern);
        List<List<String>> patternTokenNGrams = patternTokens.stream()
            .map(token -> TokenUtils.generateNGrams(token.toLowerCase(), 2))
            .filter(ngrams -> !ngrams.isEmpty())
            .collect(Collectors.toList());

        List<String> patternNGrams = patternTokenNGrams.stream().flatMap(List::stream).collect(Collectors.toList());

        Aggregation aggregation = Aggregation.newAggregation(
            //Filter the candidate
            Aggregation.match(
                Criteria.where("token_ngrams").in(patternNGrams)
            ),

            //Calculate number of ngrams overlaps
            Aggregation.addFields().addFieldWithValue("matchScores",
                patternTokenNGrams.stream()
                    .map(ngrams -> new Document("$sum",
                        ngrams.stream()
                            .map(ngram -> new Document("$cond",
                                Arrays.asList(
                                    new Document("$in", Arrays.asList(ngram, "$token_ngrams")),
                                    1,
                                    0
                                )
                            ))
                            .collect(Collectors.toList())   
                    ))
                    .collect(Collectors.toList())
            ).build(),

            //Get the product with the scores > 0.8
            Aggregation.match(new Criteria().andOperator(
                patternTokenNGrams.stream()
                    .map(ngrams -> Criteria.where("matchScores." + patternTokenNGrams.indexOf(ngrams))
                        .gte((double) Math.ceil(ngrams.size() * 0.8)))
                    .collect(Collectors.toList())
                    .toArray(new Criteria[0])
            )),

            Aggregation.limit(1)
        );

        AggregationResults<Ingredient> ingredients = mongoTemplate.aggregate(aggregation, collectionName, Ingredient.class);
        List<Ingredient> result = ingredients.getMappedResults();

        if (result.size() > 0) return result.get(0);
        return new Ingredient();
    }
}
