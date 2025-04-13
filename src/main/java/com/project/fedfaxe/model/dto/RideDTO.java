package com.project.fedfaxe.model.dto;

import com.project.fedfaxe.model.RideProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideDTO {

    private String id;
    private String rideType;
    private String vehicleInfo;
    private int passengerCapacity;
    private int luggageCapacity;
    private List<String> amenities;
    private String productImageUrl;
    private double price;
    private String pricingInfo;
    private String currency;
    private List<String> policies;



    public static RideDTO fromRideProduct(RideProduct rideProduct) {
        // Calculate price based only on price per km
        double calculatedPrice = rideProduct.getPricePerKm() * 10; // Example 10km

        // Update pricing info to only show per km rate
        String pricingInfo = "₦" + rideProduct.getPricePerKm() + " per km";

        return RideDTO.builder()
                .id(rideProduct.getId() != null ? rideProduct.getId() : UUID.randomUUID().toString())
                .rideType(rideProduct.getRideType())
                .passengerCapacity(rideProduct.getPassengerCapacity())
                .luggageCapacity(rideProduct.getLuggageCapacity())
                .amenities(rideProduct.getAmenities())
                .productImageUrl(rideProduct.getProductImageUrl())
                .price(calculatedPrice)
                .pricingInfo(pricingInfo)
                .currency(rideProduct.getCurrency())
                .policies(rideProduct.getPolicies())
                .build();
    }

public static List<RideDTO> fromRideProducts(List<RideProduct> rideProducts) {
    return rideProducts.stream()
            .map(RideDTO::fromRideProduct)
            .collect(Collectors.toList());
}
}
