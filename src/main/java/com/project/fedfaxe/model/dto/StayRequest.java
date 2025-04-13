package com.project.fedfaxe.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.fedfaxe.model.RoomCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private String city;
    private String country;
    private int starRating;
    private String propertyType;
    private List<String> amenities;
    private List<String> imageUrls;
    private List<RoomCategory> roomCategories;


}
