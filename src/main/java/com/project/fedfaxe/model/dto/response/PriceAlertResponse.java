package com.project.fedfaxe.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlertResponse {

    private String alertId;
    private String packageId;
    private String userId;
    private BigDecimal targetPrice;
    private String email;
    private LocalDateTime created;
}
