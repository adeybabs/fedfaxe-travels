package com.project.fedfaxe.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightSearchRequests {

    private String origin;
    private String destination;
    private String departureDate;
    private String returnDate; // Optional
    private int adults;
    private boolean directFlightOnly = false;
    private String sortBy = "cheapest"; // Default sorting method
    private String travelClass = "ECONOMY";

}
