package com.project.fedfaxe.model.dto;

import com.project.fedfaxe.model.enums.TravelClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightSearchResponse {
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String departureAirport;
    private String arrivalAirport;
    private String flightType;
    private String airline;
    private String pricePerAdult;
    private TravelClass travelClass;
    private int stops;

}
