package com.project.fedfaxe.controller;


import com.project.fedfaxe.service.AmadeusFlightService;
import com.project.fedfaxe.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    //private final FlightService flightService;
    private final AmadeusFlightService amadeusFlightService;

    public FlightController(FlightService flightService, AmadeusFlightService amadeusFlightService){
        //this.flightService = flightService;
        this.amadeusFlightService = amadeusFlightService;
    }


    @GetMapping("/search")
    public ResponseEntity<String> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam int adults) {

        String flightData = amadeusFlightService.searchFlights(origin, destination, departureDate, adults);
        return ResponseEntity.ok(flightData);
    }


}
