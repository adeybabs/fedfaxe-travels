package com.project.fedfaxe.controller;


import com.project.fedfaxe.model.Flight;
import com.project.fedfaxe.model.dto.FlightSearchRequests;
import com.project.fedfaxe.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }

//    private final AmadeusFlightService amadeusFlightService;

//    public FlightController(AmadeusFlightService amadeusFlightService) {
//        this.amadeusFlightService = amadeusFlightService;
//    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>>searchFlights(@RequestBody FlightSearchRequests requests){
        return ResponseEntity.ok(flightService.searchFlights(requests));
    }

//    @GetMapping("/search")
//    public FlightOfferSearch[] searchFlights(
//            @RequestParam String from,
//            @RequestParam String to,
//            @RequestParam String departureDate,
//            @RequestParam int adults) {
//        return amadeusFlightService.searchFlights(from, to, departureDate, adults);
//    }


}
