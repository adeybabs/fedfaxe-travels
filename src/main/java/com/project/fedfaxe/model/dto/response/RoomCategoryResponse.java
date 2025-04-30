package com.project.fedfaxe.model.dto.response;

import com.project.fedfaxe.model.RoomCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCategoryResponse {

    private String id;
    private String type;
    private double price;
    private int unitsAvailable;

    // Convert from RoomCategory Entity to Response DTO
    public RoomCategoryResponse(RoomCategory roomCategory) {
        this.id = roomCategory.getId();
        this.type = roomCategory.getType();
        this.price = roomCategory.getPrice();
        this.unitsAvailable = roomCategory.getUnitsAvailable();
    }
}
