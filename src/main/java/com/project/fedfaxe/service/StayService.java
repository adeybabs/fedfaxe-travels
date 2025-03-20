package com.project.fedfaxe.service;

import com.project.fedfaxe.exception.ResourceNotFoundException;
import com.project.fedfaxe.model.RoomCategory;
import com.project.fedfaxe.model.Stay;
import com.project.fedfaxe.model.dto.RoomCategoryResponse;
import com.project.fedfaxe.model.dto.StayRequest;
import com.project.fedfaxe.model.dto.StayResponse;
import com.project.fedfaxe.repository.RoomCategoryRepository;
import com.project.fedfaxe.repository.StayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StayService {

    private final StayRepository stayRepository;
    private final RoomCategoryRepository roomCategoryRepository;


    public StayResponse createStay(StayRequest request) {
        Stay stay = new Stay();
        stay.setName(request.getName());
        stay.setDescription(request.getDescription());
        stay.setAddress(request.getAddress());
        stay.setStarRating(request.getStarRating());
        stay.setAmenities(request.getAmenities());
        stay.setPropertyType(request.getPropertyType());
        stay.setImages(request.getImages());

        // Ensure roomCategories are saved first
        List<RoomCategory> roomCategories = request.getRoomCategories();
        if (roomCategories != null && !roomCategories.isEmpty()) {
            roomCategories = roomCategoryRepository.saveAll(roomCategories);
            stay.setRoomCategories(roomCategories);
        }

        stayRepository.save(stay);
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
                .starRating(stay.getStarRating())
                .propertyType(stay.getPropertyType())
                .amenities(stay.getAmenities())
                .images(stay.getImages())
                .roomCategories(
                        stay.getRoomCategories().stream()
                                .map(RoomCategoryResponse::new)
                                .toList()
                )
                .build();
    }


}
