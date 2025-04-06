package com.project.fedfaxe.model.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@Data
public class RideProductRequest {

    private String rideType;
    private int passengerCapacity;
    private int luggageCapacity;
    private List<String> amenities;
    private String productImage; // Store image as URL or base64 string
    private double pricePerKm;
    private String currency;
    private List<String> policies;
    @Indexed
    private String city;
    @Indexed
    private String country;
}
