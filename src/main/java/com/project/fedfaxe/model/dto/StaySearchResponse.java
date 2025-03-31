package com.project.fedfaxe.model.dto;

import com.project.fedfaxe.model.RoomCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaySearchResponse {

    private String id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private int starRating;
    private String propertyType;
    private List<String> amenities;
    private List<String> images;
    private LocalDate checkIn;  // ✅ Added Check-in
    private LocalDate checkOut; // ✅ Added Check-out
    private List<RoomCategory> roomCategories;
}
