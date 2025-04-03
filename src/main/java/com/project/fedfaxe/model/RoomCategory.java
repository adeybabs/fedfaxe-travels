package com.project.fedfaxe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "room_categories")
public class RoomCategory {

    @Id
    private String id;
    private String type;
    private double price;
    private int unitsAvailable;
}
