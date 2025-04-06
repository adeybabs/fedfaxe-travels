package com.project.fedfaxe.service;

import com.project.fedfaxe.model.Airport;
import com.project.fedfaxe.model.dto.CitySearchResult;
import com.project.fedfaxe.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
                        airport.getCountry(),
                        airport.getIataCode()))
                .distinct()
                .collect(Collectors.toList());
    }


    public Optional<String> getIataCodeForCity(String cityName) {
        return airportRepository.findByCityContainingIgnoreCase(cityName)
                .stream()
                .findFirst()
                .map(Airport::getIataCode);
    }

    public Optional<String> getCityForIataCode(String iataCode) {
        return airportRepository.findByIataCode(iataCode)
                .map(Airport::getCity);
    }
}
