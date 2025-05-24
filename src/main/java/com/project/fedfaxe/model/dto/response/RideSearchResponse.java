package com.project.fedfaxe.model.dto.response;

import com.project.fedfaxe.model.dto.RideDTO;
import com.project.fedfaxe.model.enums.JourneyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideSearchResponse {

    private JourneyType journeyType;
    private String departureDate;
    private String pickupTime;
    private String pickupLocation;
    private String dropoffLocation;

    private Page<RideDTO> rides;
}
