package com.project.fedfaxe.model.dto.response;

import com.project.fedfaxe.model.RideProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideProductResponse {

    private String id;
    private String rideType;
    private int passengerCapacity;
    private int luggageCapacity;
    private List<String> amenities;
    private String productImageUrl;
    private double pricePerKm;
    private String currency;
    private List<String> policies;


    public RideProductResponse(RideProduct rideProduct) {
        this.id = rideProduct.getId();
        this.rideType = rideProduct.getRideType();
        this.passengerCapacity = rideProduct.getPassengerCapacity();
        this.luggageCapacity = rideProduct.getLuggageCapacity();
        this.amenities = rideProduct.getAmenities();
        this.productImageUrl = rideProduct.getProductImageUrl();
        this.pricePerKm = rideProduct.getPricePerKm();
        this.currency = rideProduct.getCurrency();
        this.policies = rideProduct.getPolicies();
    }
}
