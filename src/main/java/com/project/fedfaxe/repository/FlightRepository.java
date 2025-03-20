package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.Flight;
import com.project.fedfaxe.model.enums.TravelClass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends MongoRepository<Flight, String> {

    List<Flight> findByDepartureAirportAndArrivalAirportAndDepartureTimeBetweenAndTravelClass(
            String departureAirport,
            String arrivalAirport,
            LocalDateTime startDate,
            LocalDateTime endDate,
            TravelClass travelClass
    );
}
