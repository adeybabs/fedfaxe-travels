package com.project.fedfaxe.service;

import com.project.fedfaxe.model.Airport;
import com.project.fedfaxe.model.dto.AirportSearchResult;
import com.project.fedfaxe.model.dto.CitySearchResult;
import com.project.fedfaxe.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;


    public List<CitySearchResult> searchCities(String query) {
        return airportRepository.findByCityContainingIgnoreCase(query)
                .stream()
                .map(airport -> new CitySearchResult(
                        airport.getCity(),
                        airport.getName(),
                        airport.getCountry(),
                        airport.getIataCode()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<AirportSearchResult> searchAirports(String query) {
        return airportRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(airport -> new AirportSearchResult(
                        airport.getName(),
                        airport.getIataCode(),
                        airport.getCity(),
                        airport.getCountry()))
                .distinct()
                .collect(Collectors.toList());
    }


    public Map<String, List<?>> combinedSearch(String query) {
        // Search cities by name
        List<CitySearchResult> cities = airportRepository.findByCityContainingIgnoreCase(query)
                .stream()
                .map(airport -> new CitySearchResult(
                        airport.getCity(),
                        airport.getName(),
                        airport.getCountry(),
                        airport.getIataCode()))
                .distinct()
                .collect(Collectors.toList());

        // Search airports by name
        List<AirportSearchResult> airports = airportRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(airport -> new AirportSearchResult(
                        airport.getName(),
                        airport.getIataCode(),
                        airport.getCity(),
                        airport.getCountry()))
                .distinct()
                .collect(Collectors.toList());

        // Add search by IATA code
        airportRepository.findByIataCodeContainingIgnoreCase(query)
                .stream()
                .map(airport -> new AirportSearchResult(
                        airport.getName(),
                        airport.getIataCode(),
                        airport.getCity(),
                        airport.getCountry()))
                .forEach(result -> {
                    if (!airports.contains(result)) {
                        airports.add(result);
                    }
                });

        Map<String, List<?>> results = new HashMap<>();
        results.put("cities", cities);
        results.put("airports", airports);
        return results;
    }


//    public Optional<String> getIataCodeForCity(String cityName) {
//        return airportRepository.findByCityContainingIgnoreCase(cityName)
//                .stream()
//                .findFirst()
//                .map(Airport::getIataCode);
//    }
//
//    public Optional<String> getCityForIataCode(String iataCode) {
//        return airportRepository.findByIataCode(iataCode)
//                .map(Airport::getCity);
//    }
}
