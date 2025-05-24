package com.project.fedfaxe.service;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.model.PriceAlert;
import com.project.fedfaxe.model.enums.PackageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PackageService {

    Page<PackageProduct> searchPackages(
            String destination,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfTravelers,
            Integer minNights,
            Integer maxNights,
            Boolean withFlights,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            List<PackageType> packageTypes,
            PageRequest pageRequest);

    Map<PackageType, Long> getPackageTypeCounts(
            String destination,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfTravelers,
            Integer minNights,
            Integer maxNights,
            Boolean withFlights,
            BigDecimal minBudget,
            BigDecimal maxBudget);

    PriceAlert createPriceAlert(String packageId, String userId, BigDecimal targetPrice, String email);
}
