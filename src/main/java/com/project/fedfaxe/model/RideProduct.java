package com.project.fedfaxe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class RideProduct {

    @Id
    private String id;
    private String rideType;
    private int passengerCapacity;
    private int luggageCapacity;
    private List<String> amenities;
    private String productImage;
    private double pricePerKm;
    private String currency;
    private List<String> policies;


    // Optional route-specific fields
    private String fromCity;
    private String toCity;
    private Double distanceInKm;
    private Boolean isAirportTransfer;
    private Boolean isHourlyRental;
    private List<LocalDateTime> availableTimes;
}
