package com.project.fedfaxe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "price_alerts")
public class PriceAlert {

    @Id
    private String id;
    private String packageId;
    private String userId;
    private BigDecimal targetPrice;
    private String email;
    private LocalDateTime created;
    private LocalDateTime triggered;
    private Boolean isActive = true;
}
