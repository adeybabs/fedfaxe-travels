package com.project.fedfaxe.model.dto;

import com.project.fedfaxe.model.Stay;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StayResponse {

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
    private List<RoomCategoryResponse> roomCategories;

    // Convert from Stay Entity to Response DTO
    public StayResponse(Stay stay) {
        this.id = stay.getId();
        this.name = stay.getName();
        this.description = stay.getDescription();
        this.address = stay.getAddress();
        this.city = stay.getCity();
        this.country = stay.getCountry();
        this.starRating = stay.getStarRating();
        this.propertyType = stay.getPropertyType();
        this.amenities = stay.getAmenities();
        this.images = stay.getImages();
        this.roomCategories = (stay.getRoomCategories() != null)
                ? stay.getRoomCategories().stream()
                .map(RoomCategoryResponse::new)
                .collect(Collectors.toList())
                : List.of(); // Prevents NullPointerException
    }
}
