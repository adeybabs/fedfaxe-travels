package com.project.fedfaxe.model.dto;


import com.project.fedfaxe.model.RoomCategory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StayRequest {

    private String name;
    private String description;
    private String address;
    private int starRating;
    private String propertyType;
    private List<String> amenities;
    private List<String> images;
    private List<RoomCategory> roomCategories;


}
