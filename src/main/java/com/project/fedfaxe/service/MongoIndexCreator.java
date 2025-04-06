package com.project.fedfaxe.service;

import com.project.fedfaxe.model.Airport;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexCreator {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        mongoTemplate.indexOps(Airport.class).ensureIndex(
                new Index().on("iataCode", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(Airport.class).ensureIndex(
                new Index().on("city", Sort.Direction.ASC));
        mongoTemplate.indexOps(Airport.class).ensureIndex(
                new Index().on("country", Sort.Direction.ASC));
    }
}
