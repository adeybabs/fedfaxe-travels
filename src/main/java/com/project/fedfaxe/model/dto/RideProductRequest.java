package com.project.fedfaxe.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.web.multipart.MultipartFile;

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
