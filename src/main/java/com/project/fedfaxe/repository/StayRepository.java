package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.Stay;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StayRepository extends MongoRepository<Stay, String> {

    List<Stay> findByCityIgnoreCase(String city);
    List<Stay> findByCountryIgnoreCase(String country);
    List<Stay> findByCityIgnoreCaseOrCountryIgnoreCase(String city, String country);
}
