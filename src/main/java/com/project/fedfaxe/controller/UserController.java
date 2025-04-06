package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.RideProduct;
import com.project.fedfaxe.model.dto.RideDTO;
import com.project.fedfaxe.model.dto.StayResponse;
import com.project.fedfaxe.model.dto.StaySearchRequest;
import com.project.fedfaxe.model.dto.StaySearchResponse;
import com.project.fedfaxe.service.RideProductService;
import com.project.fedfaxe.service.StayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            description = "This endpoint searches for available rides based on city, ride type, and other filters. It returns paginated results of ride products.",
            parameters = {
                    @Parameter(name = "fromCity", description = "The departure city for the ride", required = true),
                    @Parameter(name = "toCity", description = "The destination city for the ride", required = false),
                    @Parameter(name = "rideType", description = "The type of ride (e.g., private, shared)", required = false),
                    @Parameter(name = "passengers", description = "Number of passengers", required = false),
                    @Parameter(name = "luggage", description = "Amount of luggage (if any)", required = false),
                    @Parameter(name = "page", description = "Page number for pagination (defaults to 0)", required = false),
                    @Parameter(name = "size", description = "Page size for pagination (defaults to 10)", required = false),
                    @Parameter(name = "sortBy", description = "The field to sort by (defaults to pricePerKm)", required = false),
                    @Parameter(name = "sortDirection", description = "Sort direction (asc or desc, defaults to asc)", required = false)
            },
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
    public ResponseEntity<Page<RideDTO>> getRideResults(
            @RequestParam String fromCity,
            @RequestParam(required = false) String toCity,
            @RequestParam(required = false) String rideType,
            @RequestParam(required = false) Integer passengers,
            @RequestParam(required = false) Integer luggage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pricePerKm") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Get ride products from database
        Page<RideProduct> rideProducts = rideService.searchRideProducts(
                fromCity, toCity, rideType, passengers, luggage, pageRequest);

        // Convert to DTOs
        List<RideDTO> rideDTOs = RideDTO.fromRideProducts(rideProducts.getContent());

        // Create new page with DTOs
        Page<RideDTO> dtoPage = new PageImpl<>(
                rideDTOs,
                pageRequest,
                rideProducts.getTotalElements()
        );

        return ResponseEntity.ok(dtoPage);
    }

//    @GetMapping("rides/search")
//    public ResponseEntity<Page<RideProduct>> searchRides(
//            @RequestParam() String fromCity,
//            @RequestParam(required = false) String toCity,
//            @RequestParam(required = false) String rideType,
//            @RequestParam(required = false) Integer passengerCapacity,
//            @RequestParam(required = false) Integer luggageCapacity,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "pricePerKm") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDirection) {
//
//        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
//
//        Page<RideProduct> rides = rideService.searchRideProducts(
//                fromCity, toCity, rideType, passengerCapacity, luggageCapacity, pageRequest);
//
//        return ResponseEntity.ok(rides);
//    }




}
