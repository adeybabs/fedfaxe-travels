package com.project.fedfaxe.service;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.model.dto.*;
import com.project.fedfaxe.repository.PackageProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PackageProductService {

    private final PackageProductRepository packageProductRepository;


    public PackageResponse addPackage(PackageRequest request) {

        // Create the PackageProduct object with image URLs
        PackageProduct packageProduct = PackageProduct.builder()
                .productName(request.getProductName())
                .location(request.getLocation())
                .luggageCapacity(request.getLuggageCapacity())
                .activities(request.getActivities())
                .amenities(request.getAmenities())
                .imageUrls(request.getImageUrls())
                .priceWithFlights(request.getPriceWithFlights())
                .priceWithoutFlights(request.getPriceWithoutFlights())
                .currency(request.getCurrency())
                .build();

        // Save the package product to the database
        packageProduct = packageProductRepository.save(packageProduct);
        return new PackageResponse(packageProduct);
    }


    public Optional<PackageProduct> getPackageById(String id) {
        return packageProductRepository.findById(id);
    }

    public boolean deletePackage(String id) {
        if (packageProductRepository.existsById(id)) {
            packageProductRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<PackageProduct> getAllPackages() {
        return packageProductRepository.findAll();
    }

}
