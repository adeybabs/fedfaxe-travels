package com.project.fedfaxe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightSearchRequests {

    private String departureAirportCode;  // e.g., "LOS" (Lagos)
    private String arrivalAirportCode;    // e.g., "JFK" (New York)
    private LocalDate departureDate;      // e.g., 2024-06-15
    private LocalDate returnDate;         // Optional for round trips
    private int passengerCount;           // Number of passengers
    private String cabinClass;            // Economy, Business, First Class
    private String airlineCode;           // Optional: Filter by airline (e.g., "BA" for British Airways)

}
