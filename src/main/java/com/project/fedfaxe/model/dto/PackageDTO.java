package com.project.fedfaxe.model.dto;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.model.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {

    private String id;
    private String productName;
    private String location;
    private Integer duration; // Nights
    private List<String> activities;
    private List<String> amenities;
    private List<String> imageUrls;
    private BigDecimal priceWithFlights;
    private BigDecimal priceWithoutFlights;
    private String currency;
    private PackageType packageType;

    private Integer starRating;
    private List<String> inclusions;
    private List<String> specialFeatures;

    public static PackageDTO fromPackageProduct(PackageProduct product) {
        return PackageDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .location(product.getLocation())
                .duration(product.getDuration())
                .activities(product.getActivities())
                .amenities(product.getAmenities())
                .imageUrls(product.getImageUrls())
                .priceWithFlights(BigDecimal.valueOf(product.getPriceWithFlights()))
                .priceWithoutFlights(BigDecimal.valueOf(product.getPriceWithoutFlights()))
                .currency(product.getCurrency())
                .packageType(product.getPackageType())
                .starRating(product.getStarRating())
                .inclusions(product.getInclusions())
                .specialFeatures(product.getSpecialFeatures())
                .build();
    }

    public static List<PackageDTO> fromPackageProducts(List<PackageProduct> products) {
        return products.stream()
                .map(PackageDTO::fromPackageProduct)
                .collect(Collectors.toList());
    }
}
