package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.dto.StayResponse;
import com.project.fedfaxe.model.dto.StaySearchRequest;
import com.project.fedfaxe.model.dto.StaySearchResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User Management", description = "Endpoints for managing user searches and other unauthenticated user flows")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final StayService stayService;

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
}
