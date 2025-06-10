package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComSegment {

    private BookingComAirport departureAirport;
    private BookingComAirport arrivalAirport;
    private String departureTime; // ISO format: 2025-06-15T06:40:00
    private String arrivalTime;   // ISO format: 2025-06-15T07:50:00
    private List<BookingComLeg> legs;
    private int totalTime; // in seconds
}
