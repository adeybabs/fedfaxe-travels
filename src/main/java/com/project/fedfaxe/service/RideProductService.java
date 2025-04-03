package com.project.fedfaxe.service;

import com.project.fedfaxe.model.RideProduct;
import com.project.fedfaxe.model.dto.RideProductRequest;
import com.project.fedfaxe.model.dto.RideProductResponse;
import com.project.fedfaxe.model.dto.StayResponse;
import com.project.fedfaxe.repository.RideProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideProductService {

    private final RideProductRepository rideProductRepository;

    public RideProductResponse addRideProduct(RideProductRequest request) {
        RideProduct rideProduct = RideProduct.builder()
                .rideType(request.getRideType())
                .passengerCapacity(request.getPassengerCapacity())
                .luggageCapacity(request.getLuggageCapacity())
                .amenities(request.getAmenities())
                .productImage(request.getProductImage())
                .pricePerKm(request.getPricePerKm())
                .currency(request.getCurrency())
                .policies(request.getPolicies())
                .build();

        rideProduct = rideProductRepository.save(rideProduct);
        return mapToRideProductResponse(rideProduct);
    }

    public RideProductResponse getRideProductById(String id) {
        RideProduct rideProduct = rideProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride product not found"));

        return mapToRideProductResponse(rideProduct);
    }
//    public RideProductResponse getRideProductById(String id) {
//        RideProduct rideProduct = rideProductRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride product not found"));
//
//        return new RideProductResponse(rideProduct);
//    }


    public List<RideProductResponse> getAllRideProducts() {
        return rideProductRepository.findAll().stream().map(this::mapToRideProductResponse).toList();
    }


    private RideProductResponse mapToRideProductResponse(RideProduct rideProduct) {
        return RideProductResponse.builder()
                .id(rideProduct.getId())
                .rideType(rideProduct.getRideType())
                .passengerCapacity(rideProduct.getPassengerCapacity())
                .luggageCapacity(rideProduct.getLuggageCapacity())
                .amenities(rideProduct.getAmenities())
                .productImage(rideProduct.getProductImage())
                .pricePerKm(rideProduct.getPricePerKm())
                .currency(rideProduct.getCurrency())
                .policies(rideProduct.getPolicies())
                .build();
    }



    public void deleteRideProduct(String id) {
        if (!rideProductRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride product not found");
        }
        rideProductRepository.deleteById(id);
    }
}
