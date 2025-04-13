package com.project.fedfaxe.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.web.multipart.MultipartFile;

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
