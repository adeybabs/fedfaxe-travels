package com.project.fedfaxe.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlertRequest {

    @NotNull
    private String packageId;

    @NotNull
    private String userId;

    @NotNull
    private BigDecimal targetPrice;

    @NotNull
    @Email
    private String email;
}
