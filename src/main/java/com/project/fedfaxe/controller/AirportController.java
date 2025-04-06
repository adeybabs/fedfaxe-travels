package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.dto.CitySearchResult;
import com.project.fedfaxe.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airport")
public class AirportController {

    @Autowired
    private AirportService airportService;


    @Operation(
            summary = "Search for cities by name",
            description = "Returns a list of cities based on a search query",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of cities found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CitySearchResult.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "No cities found")
            }
    )
    @GetMapping("/cities/search")
    public List<CitySearchResult> searchCities(@RequestParam String query) {
        return airportService.searchCities(query);
    }


    @Operation(
            summary = "Get IATA code for a city",
            description = "Returns the IATA code for the provided city name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "IATA code found"),
                    @ApiResponse(responseCode = "404", description = "City not found")
            }
    )
    @GetMapping("/city/code")
    public ResponseEntity<String> getIataCode(@RequestParam String city) {
        return airportService.getIataCodeForCity(city)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get city by IATA code",
            description = "Returns the city corresponding to the provided IATA code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "City found"),
                    @ApiResponse(responseCode = "404", description = "IATA code not found")
            }
    )
    @GetMapping("/code/city")
    public ResponseEntity<String> getCity(@RequestParam String code) {
        return airportService.getCityForIataCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
