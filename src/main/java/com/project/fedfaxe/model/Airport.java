package com.project.fedfaxe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "airports")
public class Airport {
    @Id
    private String id;

    private String name;
    private String city;
    private String country;
    private String iataCode; // e.g., LOS for Lagos, ABV for Abuja
}
