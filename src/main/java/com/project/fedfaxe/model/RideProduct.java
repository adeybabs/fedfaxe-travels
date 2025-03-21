package com.project.fedfaxe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ride_products")
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
}
