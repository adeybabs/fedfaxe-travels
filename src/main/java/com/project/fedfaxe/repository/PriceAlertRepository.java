package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.PriceAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceAlertRepository extends MongoRepository<PriceAlert, String> {
}
