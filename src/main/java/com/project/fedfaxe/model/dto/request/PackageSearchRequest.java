package com.project.fedfaxe.model.dto.request;

import com.project.fedfaxe.model.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageSearchRequest {

    // Main search parameters
    private String destination;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfTravelers;

    // Filter parameters
    private Integer minNights;
    private Integer maxNights;
    private Boolean withFlights;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private String currency;

    // Package type filtering
    private List<PackageType> packageTypes; // ALL, HONEYMOON, FAMILY, LUXURY

    // Sorting
    private String sortBy; // e.g., "price", "rating", "duration"
    private String sortDirection; // "asc", "desc"

    // Pagination
    private Integer page;
    private Integer size;
}
