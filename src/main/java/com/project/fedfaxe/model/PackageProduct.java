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
@Document(collection = "package_products")
public class PackageProduct {

    @Id
    private String id;
    private String productName;
    private String location;
    private int luggageCapacity;
    private List<String> activities;
    private List<String> amenities;
    private List<String> productImages;
    private double priceWithFlights;
    private double priceWithoutFlights;
    private String currency;
}
