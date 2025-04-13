package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.dto.RideProductRequest;
import com.project.fedfaxe.model.dto.RideProductResponse;
import com.project.fedfaxe.model.dto.StayResponse;
import com.project.fedfaxe.service.RideProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/rides")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class RideAdminController {

    private final RideProductService rideProductService;



    @Operation(summary = "Add a new ride product", description = "Creates a new ride product with previously uploaded image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ride product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<RideProductResponse> addRideProduct(
            @RequestBody @Valid RideProductRequest request) {

        // Call service to create the ride product
        RideProductResponse response = rideProductService.addRideProduct(request);
        return ResponseEntity.ok(response);
    }




    @Operation(summary = "Get a ride product by ID", description = "Retrieves the ride product details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ride product found"),
            @ApiResponse(responseCode = "404", description = "Ride product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RideProductResponse> getRideById(@PathVariable String id) {
        RideProductResponse response = rideProductService.getRideProductById(id);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get all ride products",
            description = "Retrieves a list of all available ride products.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of ride products retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RideProductResponse.class)))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<List<RideProductResponse>> getAllRides() {
        List<RideProductResponse> rides = rideProductService.getAllRideProducts();
        return ResponseEntity.ok(rides);
    }



    @Operation(summary = "Delete a ride product", description = "Deletes a ride product by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ride product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ride product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRideById(@PathVariable String id) {
        rideProductService.deleteRideProduct(id);
        return ResponseEntity.ok().build();
    }
}
