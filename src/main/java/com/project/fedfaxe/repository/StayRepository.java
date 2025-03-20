package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.Stay;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StayRepository extends MongoRepository<Stay, String> {
}
