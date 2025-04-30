package com.project.fedfaxe.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PackageRequest {

    private String productName;
    private String location;
    private int luggageCapacity;
    private List<String> activities;
    private List<String> amenities;
    private List<String> imageUrls;
    private double priceWithFlights;
    private double priceWithoutFlights;
    private String currency;
}
