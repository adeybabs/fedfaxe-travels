package com.project.fedfaxe.model;


import com.project.fedfaxe.model.enums.TravelClass;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection= "flights")
public class Flight {

    @Id
    private String id; // MongoDB automatically generates an ObjectId

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;

    private String departureAirport;
    private String arrivalAirport;
    private String flightType;
    private String airline;

    private TravelClass travelClass; // Enum for class type

    private BigDecimal pricePerAdult; // Using BigDecimal for accuracy
    private int stops;
}
