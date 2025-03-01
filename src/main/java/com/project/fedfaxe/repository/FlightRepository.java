package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends MongoRepository<Flight, String> {

    List<Flight> findByDepartureAirportCodeAndArrivalAirportCodeAndDepartureTimeBetweenAndCabinClass(
            String departureAirportCode,
            String arrivalAirportCode,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String cabinClass
    );
}
