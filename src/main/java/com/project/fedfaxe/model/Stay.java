package com.project.fedfaxe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "stays")
public class Stay {

    @Id
    private String id;

    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private int starRating;
    private String propertyType;
    private List<String> amenities;
    private List<String> imageUrls;
    @DBRef(lazy = true)
    private List<RoomCategory> roomCategories;

}
