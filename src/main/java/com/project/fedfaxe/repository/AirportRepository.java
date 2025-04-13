package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.Airport;
import com.project.fedfaxe.model.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends MongoRepository<Airport, String> {
    List<Airport> findByCityContainingIgnoreCase(String cityName);
    List<Airport> findByNameContainingIgnoreCase(String airportName);
    Optional<Airport> findByIataCode(String iataCode);
}
