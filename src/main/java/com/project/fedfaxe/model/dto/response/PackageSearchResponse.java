package com.project.fedfaxe.model.dto.response;

import com.project.fedfaxe.model.dto.PackageDTO;
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
public class PackageSearchResponse {

    // Search metadata
    private String destination;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfTravelers;

    // Category counts (for UI tabs)
    private Integer totalPackages;
    private Integer honeymoonPackages;
    private Integer familyPackages;
    private Integer luxuryPackages;

    // Filter summary
    private Integer minNights;
    private Integer maxNights;
    private Boolean withFlights;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;

    // Results
    private List<PackageDTO> packages;

    // Pagination info
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
}
