package com.project.fedfaxe.service;

import com.project.fedfaxe.model.RideProduct;
import com.project.fedfaxe.model.dto.request.RideProductRequest;
import com.project.fedfaxe.model.dto.response.RideProductResponse;
import com.project.fedfaxe.repository.RideProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RideProductService {

    private final RideProductRepository rideProductRepository;
    private final MongoTemplate mongoTemplate;


    public RideProductResponse addRideProduct(RideProductRequest request) {
        RideProduct rideProduct = RideProduct.builder()
                .rideType(request.getRideType())
                .passengerCapacity(request.getPassengerCapacity())
                .luggageCapacity(request.getLuggageCapacity())
                .amenities(request.getAmenities())
                .productImageUrl(request.getProductImageUrl())
                .pricePerKm(request.getPricePerKm())
                .currency(request.getCurrency())
                .policies(request.getPolicies())
                .build();

        // Save the ride product to the repository
        rideProduct = rideProductRepository.save(rideProduct);
        return mapToRideProductResponse(rideProduct);
    }


    public RideProductResponse getRideProductById(String id) {
        RideProduct rideProduct = rideProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride product not found"));

        return mapToRideProductResponse(rideProduct);
    }


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
                .productImageUrl(rideProduct.getProductImageUrl())
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


    public Page<RideProduct> searchRideProducts(
            PageRequest pageRequest) {

        // Create criteria for the search
        Criteria criteria = new Criteria();

        // Create the query with the criteria and apply pagination and sorting from PageRequest
        Query query = new Query(criteria);
        query.with(pageRequest);

        List<RideProduct> rideProductsList = mongoTemplate.find(query, RideProduct.class);

        // Create a PageImpl object to return the paginated result
        return new PageImpl<>(rideProductsList, pageRequest, mongoTemplate.count(query, RideProduct.class));
    }

}
