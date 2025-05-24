package com.project.fedfaxe.model;

import com.project.fedfaxe.model.enums.PackageType;
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

//    @Id
//    private String id;
//    private String productName;
//    private String location;
//    private int luggageCapacity;
//    private List<String> activities;
//    private List<String> amenities;
//    private List<String> imageUrls;
//    private double priceWithFlights;
//    private double priceWithoutFlights;
//    private String currency;

    @Id
    private String id;
    private String productName;
    private String location;
    private Integer duration; // Number of nights
    private int luggageCapacity;
    private List<String> activities;
    private List<String> amenities;
    private List<String> imageUrls;
    private double priceWithFlights;
    private double priceWithoutFlights;
    private String currency;


    private PackageType packageType = PackageType.ALL; // Default value
    private Integer starRating;
    private List<String> inclusions;
    private List<String> specialFeatures;


    public PackageProduct(String id, String productName, String location, Integer duration,int luggageCapacity,
                          List<String> activities, List<String> amenities, List<String> imageUrls,
                          double priceWithFlights, double priceWithoutFlights, String currency,
                          Integer starRating, List<String> inclusions, List<String> specialFeatures) {
        this.id = id;
        this.productName = productName;
        this.location = location;
        this.duration = duration;
        this.activities = activities;
        this.amenities = amenities;
        this.imageUrls = imageUrls;
        this.priceWithFlights = priceWithFlights;
        this.priceWithoutFlights = priceWithoutFlights;
        this.currency = currency;
        this.starRating = starRating;
        this.inclusions = inclusions;
        this.specialFeatures = specialFeatures;
        this.luggageCapacity = luggageCapacity;
    }
}
