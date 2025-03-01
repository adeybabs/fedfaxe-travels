package com.project.fedfaxe.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection= "flights")
public class Flight {

    @Id
    private String id;

    private String airline;
    private String flightNumber;

    private String departureAirportCode; // e.g., "LOS" (Lagos)
    private String arrivalAirportCode;   // e.g., "JFK" (New York)

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;

    private String cabinClass; // Economy, Business, First Class
    private boolean directFlight;
}
