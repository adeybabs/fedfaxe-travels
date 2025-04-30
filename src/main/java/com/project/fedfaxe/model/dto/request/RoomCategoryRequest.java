package com.project.fedfaxe.model.dto.request;

import lombok.Data;

@Data
public class RoomCategoryRequest {

    private String type;
    private double price;
    private int unitsAvailable;
}
