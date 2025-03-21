package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.PackageProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PackageProductRepository extends MongoRepository<PackageProduct, String> {
}
