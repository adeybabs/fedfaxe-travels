package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.RideProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideProductRepository extends MongoRepository<RideProduct, String> {

}
