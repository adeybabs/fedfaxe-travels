package com.project.fedfaxe.controller;


import com.project.fedfaxe.model.dto.StayRequest;
import com.project.fedfaxe.model.dto.StayResponse;
import com.project.fedfaxe.service.StayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Stay Management", description = "Endpoints for managing stays (Admin Only)")
@RequiredArgsConstructor
@RequestMapping("/api/stays")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class StayAdminController {

    private final StayService stayService;

    @Operation(
            summary = "Create a new stay",
            description = "Creates a new stay and returns the created stay details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stay created successfully",
                            content = @Content(schema = @Schema(implementation = StayResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<StayResponse> createStay(@RequestBody @Valid StayRequest request) {
        return ResponseEntity.ok(stayService.createStay(request));
    }

    @Operation(
            summary = "Get stay details",
            description = "Retrieves details of a specific stay by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stay found",
                            content = @Content(schema = @Schema(implementation = StayResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Stay not found")
            }
    )
    @GetMapping("/{stayId}")
    public ResponseEntity<StayResponse> getStayById(@PathVariable String stayId) {
        StayResponse stayResponse = stayService.getStayById(stayId);
        return ResponseEntity.ok(stayResponse);
    }

    @Operation(
            summary = "Get all stays",
            description = "Retrieves a list of all available stays.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of stays retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = StayResponse.class)))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<List<StayResponse>> getAllStays() {
        List<StayResponse> stays = stayService.getAllStays();
        return ResponseEntity.ok(stays);
    }


    @Operation(
            summary = "Delete a stay",
            description = "Deletes a stay by ID. Only accessible to admins.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Stay deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Stay not found")
            }
    )
    @DeleteMapping("/{stayId}")
    public ResponseEntity<Void> deleteStay(@PathVariable String stayId) {
        stayService.deleteStay(stayId);
        return ResponseEntity.noContent().build();
    }
}
