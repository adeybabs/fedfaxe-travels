package com.project.fedfaxe.model.dto.response;

import com.project.fedfaxe.model.PackageProduct;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageResponse {
    private String id;
    private String productName;
    private String location;
    private int luggageCapacity;
    private List<String> activities;
    private List<String> amenities;
    private List<String> imageUrls;
    private double priceWithFlights;
    private double priceWithoutFlights;
    private String currency;

    public PackageResponse(PackageProduct packageProduct) {
        this.id = packageProduct.getId();
        this.productName = packageProduct.getProductName();
        this.location = packageProduct.getLocation();
        this.luggageCapacity = packageProduct.getLuggageCapacity();
        this.activities = packageProduct.getActivities();
        this.amenities = packageProduct.getAmenities();
        this.imageUrls = packageProduct.getImageUrls();
        this.priceWithFlights = packageProduct.getPriceWithFlights();
        this.priceWithoutFlights = packageProduct.getPriceWithoutFlights();
        this.currency = packageProduct.getCurrency();
    }
}
