package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.dto.RideProductRequest;
import com.project.fedfaxe.model.dto.RideProductResponse;
import com.project.fedfaxe.service.RideProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/rides")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class RideAdminController {

    private final RideProductService rideProductService;

    @Operation(summary = "Add a new ride product", description = "Creates a new ride product and returns the details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ride product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<RideProductResponse> addRideProduct(@RequestBody RideProductRequest request) {
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
