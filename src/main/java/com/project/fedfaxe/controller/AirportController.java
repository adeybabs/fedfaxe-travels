package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.dto.AirportSearchResult;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            summary = "Search for airports and cities by name",
            description = "Returns a list of airports and cities based on a search query",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of airport and cities found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CitySearchResult.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "No airports found")
            }
    )
    @GetMapping("/search")
    public Map<String, List<?>> combinedSearch(@RequestParam String query) {
        return airportService.combinedSearch(query);
    }



}
