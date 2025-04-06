package com.project.fedfaxe.service;

import com.project.fedfaxe.model.RideProduct;
import com.project.fedfaxe.model.dto.RideProductRequest;
import com.project.fedfaxe.model.dto.RideProductResponse;
import com.project.fedfaxe.repository.RideProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
                .productImage(request.getProductImage())
                .pricePerKm(request.getPricePerKm())
                .currency(request.getCurrency())
                .policies(request.getPolicies())
                .city(request.getCity())
                .country(request.getCountry())
                .build();

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


    public Page<RideProduct> searchRideProducts(
            String fromCity, String toCity, String rideType,
            Integer passengerCapacity, Integer luggageCapacity,
            Pageable pageable) {

        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        // Primary search criteria - city
        criteria.add(Criteria.where("city").is(fromCity));

        // Optional filters
        if (toCity != null && !toCity.isEmpty() && !fromCity.equals(toCity)) {
            criteria.add(Criteria.where("servesCity").is(toCity));
        }

        if (rideType != null && !rideType.isEmpty()) {
            criteria.add(Criteria.where("rideType").is(rideType));
        }

        if (passengerCapacity != null) {
            criteria.add(Criteria.where("passengerCapacity").gte(passengerCapacity));
        }

        if (luggageCapacity != null) {
            criteria.add(Criteria.where("luggageCapacity").gte(luggageCapacity));
        }

        // Combine all criteria with AND operator
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        // Execute query to get matching rides
        List<RideProduct> rides = mongoTemplate.find(query, RideProduct.class);

        // Create a count query (without pagination) to calculate total elements
        Query countQuery = new Query();
        if (!criteria.isEmpty()) {
            countQuery.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return PageableExecutionUtils.getPage(
                rides,
                pageable,
                () -> mongoTemplate.count(countQuery, RideProduct.class));
    }
}
