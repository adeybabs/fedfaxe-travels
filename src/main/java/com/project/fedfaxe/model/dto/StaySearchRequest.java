package com.project.fedfaxe.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StaySearchRequest {

    private String city;
    private String country;
    private String priceRange;  // Format: "5000-10000"
    private Integer minUnitsAvailable;
    private LocalDate checkInDate;  // Proper date type
    private LocalDate checkOutDate; // Proper date type
    private Integer minPassengers;
    private String sortBy; // Can be "cheapest", "recommended", or "highestRating"



    public Double getMinPrice() {
        if (priceRange != null && priceRange.contains("-")) {
            return Double.parseDouble(priceRange.split("-")[0].trim()); // Trim spaces
        }
        return null;
    }

    public Double getMaxPrice() {
        if (priceRange != null && priceRange.contains("-")) {
            return Double.parseDouble(priceRange.split("-")[1].trim());
        }
        return null;
    }
}
