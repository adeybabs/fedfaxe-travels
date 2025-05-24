package com.project.fedfaxe.service;

import com.project.fedfaxe.exception.ResourceNotFoundException;
import com.project.fedfaxe.model.RoomCategory;
import com.project.fedfaxe.model.Stay;
import com.project.fedfaxe.model.dto.request.StayRequest;
import com.project.fedfaxe.model.dto.request.StaySearchRequest;
import com.project.fedfaxe.model.dto.response.RoomCategoryResponse;
import com.project.fedfaxe.model.dto.response.StayResponse;
import com.project.fedfaxe.model.dto.response.StaySearchResponse;
import com.project.fedfaxe.repository.RoomCategoryRepository;
import com.project.fedfaxe.repository.StayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StayService {

    private final MongoTemplate mongoTemplate;
    private final StayRepository stayRepository;
    private final RoomCategoryRepository roomCategoryRepository;
    private final S3Service s3Service;



    public StayResponse createStay(StayRequest request) {
        // Create Stay object
        Stay stay = new Stay();
        stay.setName(request.getName());
        stay.setDescription(request.getDescription());
        stay.setAddress(request.getAddress());
        stay.setCity(request.getCity());
        stay.setCountry(request.getCountry());
        stay.setStarRating(request.getStarRating());
        stay.setAmenities(request.getAmenities());
        stay.setPropertyType(request.getPropertyType());
        stay.setImageUrls(request.getImageUrls());
        // Ensure roomCategories are saved first
        List<RoomCategory> roomCategories = request.getRoomCategories();
        if (roomCategories != null && !roomCategories.isEmpty()) {
            roomCategories = roomCategoryRepository.saveAll(roomCategories);
            stay.setRoomCategories(roomCategories);
        }

        // Save Stay object to the database
        stayRepository.save(stay);

        // Return the response
        return new StayResponse(stay);
    }


    public StayResponse getStayById(String stayId) {
        Stay stay = stayRepository.findById(stayId)
                .orElseThrow(() -> new ResourceNotFoundException("Stay not found"));

        // Fetch room categories explicitly if they are stored separately
        if (stay.getRoomCategories() != null) {
            List<RoomCategory> roomCategories = roomCategoryRepository.findAllById(
                    stay.getRoomCategories().stream().map(RoomCategory::getId).toList()
            );
            stay.setRoomCategories(roomCategories);
        }

        return new StayResponse(stay);
    }



    @Transactional
    public void deleteStay(String stayId) {
        Stay stay = stayRepository.findById(stayId)
                .orElseThrow(() -> new ResourceNotFoundException("Stay not found"));

        if (stay.getRoomCategories() != null) {
            List<String> roomCategoryIds = stay.getRoomCategories().stream()
                    .map(RoomCategory::getId)
                    .toList();
            roomCategoryRepository.deleteAllById(roomCategoryIds);
        }

        stayRepository.deleteById(stayId);
    }


    public List<StayResponse> getAllStays() {
        return stayRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    private StayResponse mapToResponse(Stay stay) {
        return StayResponse.builder()
                .id(stay.getId())
                .name(stay.getName())
                .description(stay.getDescription())
                .address(stay.getAddress())
                .city(stay.getCity())
                .country(stay.getCountry())
                .starRating(stay.getStarRating())
                .propertyType(stay.getPropertyType())
                .amenities(stay.getAmenities())
                .imageUrls(stay.getImageUrls())
                .roomCategories(
                        stay.getRoomCategories() != null ?
                                stay.getRoomCategories().stream()
                                        .filter(Objects::nonNull)  // Filter out any null room categories
                                        .map(roomCategory -> {
                                            try {
                                                return new RoomCategoryResponse(roomCategory);
                                            } catch (Exception e) {
                                                // Log the error and skip this room category
                                                log.error("Error mapping room category: {}", e.getMessage());
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)  // Filter out any failed mappings
                                        .toList()
                                : List.of()
                )
                .build();
    }


    public List<StaySearchResponse> searchStays(StaySearchRequest request) {
        Query query = new Query();

        // 🔍 Filter by City (Case-insensitive, partial match)
        if (request.getCity() != null) {
            query.addCriteria(Criteria.where("city").regex(".*" + request.getCity() + ".*", "i"));
        }

        // 🔍 Filter by Country (Case-insensitive, partial match)
        if (request.getCountry() != null) {
            query.addCriteria(Criteria.where("country").regex(".*" + request.getCountry() + ".*", "i"));
        }

        // 🔥 Execute Query
        List<Stay> stays = mongoTemplate.find(query, Stay.class);

        // 🏷️ Filter By Price Range (Apply in Java)
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            stays = stays.stream()
                    .filter(stay -> stay.getRoomCategories() != null &&
                            stay.getRoomCategories().stream()
                                    .anyMatch(room -> room.getPrice() >= request.getMinPrice()
                                            && room.getPrice() <= request.getMaxPrice()))
                    .collect(Collectors.toList());
        }

        // 🔍 Filter by Minimum Units Available in Room Categories
        if (request.getMinUnitsAvailable() != null) {
            query.addCriteria(Criteria.where("roomCategories.unitsAvailable").gte(request.getMinUnitsAvailable()));
        }

        // 🔀 Apply Sorting Based on User Selection
        if ("cheapest".equalsIgnoreCase(request.getSortBy())) {
            stays.sort(Comparator.comparingDouble(this::getMinPrice)); // Sort by lowest room price
        } else if ("highestRating".equalsIgnoreCase(request.getSortBy()) || "recommended".equalsIgnoreCase(request.getSortBy())) {
            stays.sort(Comparator.comparingInt(Stay::getStarRating).reversed()); // Highest rating first
        }

        // 🔄 Convert to StaySearchResponse and include checkIn/checkOut
        return stays.stream()
                .map(stay -> StaySearchResponse.builder()
                        .id(stay.getId())
                        .name(stay.getName())
                        .description(stay.getDescription())
                        .address(stay.getAddress())
                        .city(stay.getCity())
                        .country(stay.getCountry())
                        .starRating(stay.getStarRating())
                        .propertyType(stay.getPropertyType())
                        .amenities(stay.getAmenities())
                        .imageUrls(stay.getImageUrls())
                        .checkIn(request.getCheckInDate())  // ✅ Attach Check-in
                        .checkOut(request.getCheckOutDate()) // ✅ Attach Check-out
                        .roomCategories(stay.getRoomCategories())
                        .build()
                ).collect(Collectors.toList());
    }


    // ✅ Get the Minimum Price From Room Categories
    private Double getMinPrice(Stay stay) {
        return stay.getRoomCategories().stream()
                .map(RoomCategory::getPrice)  // Extract room prices
                .min(Double::compareTo)       // Get the lowest price
                .orElse(Double.MAX_VALUE);    // Default to max value if no rooms exist
    }
}
