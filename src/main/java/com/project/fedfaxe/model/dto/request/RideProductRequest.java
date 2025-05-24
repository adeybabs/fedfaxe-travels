package com.project.fedfaxe.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RideProductRequest {

    private String rideType;
    private int passengerCapacity;
    private int luggageCapacity;
    private List<String> amenities;
    private String productImageUrl;

    private double pricePerKm;
    private String currency;
    private List<String> policies;

}
