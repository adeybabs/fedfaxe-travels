package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.RideProduct;
import com.project.fedfaxe.model.dto.*;
import com.project.fedfaxe.model.dto.request.StaySearchRequest;
import com.project.fedfaxe.model.dto.response.RideSearchResponse;
import com.project.fedfaxe.model.dto.response.StayResponse;
import com.project.fedfaxe.model.dto.response.StaySearchResponse;
import com.project.fedfaxe.model.enums.JourneyType;
import com.project.fedfaxe.service.RideProductService;
import com.project.fedfaxe.service.StayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "User Management", description = "Endpoints for managing user searches and other unauthenticated user flows")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final StayService stayService;
    private final RideProductService rideService;

    @Operation(
            summary = "Search stays",
            description = "Search for stays based on city, country, price range, and minimum available units.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = StayResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
            }
    )
    @GetMapping("/stays/search")
    public ResponseEntity<List<StaySearchResponse>> searchStays(@Valid StaySearchRequest searchRequest) {
        List<StaySearchResponse> stayResponses = stayService.searchStays(searchRequest);
        return ResponseEntity.ok(stayResponses);
    }


    @Operation(
            summary = "Search for available rides",
            description = "This endpoint searches for available rides. It returns paginated results of ride products.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ride results found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RideDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
                    @ApiResponse(responseCode = "404", description = "No ride results found")
            }
    )
    @GetMapping("rides/search")
    public ResponseEntity<RideSearchResponse> getRideResults(
            @RequestParam(required = false) JourneyType journeyType, // journeyType for filtering
            @RequestParam(required = false) String departureDate,
            @RequestParam(required = false) String pickupTime,
            @RequestParam(required = false) String pickupLocation,
            @RequestParam(required = false) String dropoffLocation,
            @RequestParam(defaultValue = "Recommended") String sortBy, // Sorting options: Recommended, Cheapest, Highest
            @RequestParam(defaultValue = "asc") String sortDirection, // Sorting direction: asc or desc
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BadRequestException {

        if (departureDate != null) {
            LocalDate date = LocalDate.parse(departureDate);
            if (date.isBefore(LocalDate.now())) {
                throw new BadRequestException("Departure date cannot be in the past");
            }
        }
        // Set the sort direction based on 'asc' or 'desc' value
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Set the sorting logic based on the 'sortBy' parameter
        Sort sort;
        switch (sortBy.toUpperCase()) {
            case "CHEAPEST":
                // For Cheapest, we sort by 'pricePerKm' in ascending order
                sort = Sort.by(Sort.Order.asc("pricePerKm"));
                break;
            case "HIGHEST":
                // For Highest, we sort by 'pricePerKm' in descending order
                sort = Sort.by(Sort.Order.desc("pricePerKm"));
                break;
            case "RECOMMENDED":
            default:
                // For Recommended, no specific sorting (default behavior)
                sort = Sort.unsorted();  // No sorting applied for recommended
                break;
        }
        // Create a PageRequest object with the specified page size and sorting
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Get all ride products from the database with pagination and filtering
        Page<RideProduct> rideProducts = rideService.searchRideProducts(pageRequest);

        Page<RideDTO> dtoPage = new PageImpl<>(
                RideDTO.fromRideProducts(rideProducts.getContent()),
                pageRequest,
                rideProducts.getTotalElements());

        RideSearchResponse response = RideSearchResponse.builder()
                .journeyType(journeyType)
                .departureDate(departureDate)
                .pickupTime(pickupTime)
                .pickupLocation(pickupLocation)  // just echoing it back
                .dropoffLocation(dropoffLocation)
                .rides(dtoPage)
                .build();

        return ResponseEntity.ok(response);
    }


}
