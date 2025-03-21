package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.RideProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideProductRepository extends MongoRepository<RideProduct, String> {
}
