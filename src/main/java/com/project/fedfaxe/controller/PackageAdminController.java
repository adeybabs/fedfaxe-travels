package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.model.dto.*;
import com.project.fedfaxe.repository.PackageProductRepository;
import com.project.fedfaxe.service.PackageProductService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/package")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class PackageAdminController {

    private final PackageProductRepository packageProductRepository;

    private final PackageProductService packageProductService;


    @PostMapping
    @Operation(summary = "Add a new package product", description = "Creates a new package product and returns the saved object.")
    public ResponseEntity<PackageResponse> createPackage(
            @RequestBody @Valid PackageRequest request)  {

        PackageResponse response = packageProductService.addPackage(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get a package product by ID", description = "Retrieves a package product based on the given ID.")
    public ResponseEntity<PackageProduct> getPackageById(@PathVariable String id) {
        Optional<PackageProduct> packageProduct = packageProductService.getPackageById(id);
        return packageProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Get all packages",
            description = "Retrieves a list of all package products."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of package products retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<PackageProduct>> getAllPackages() {
        List<PackageProduct> packages = packageProductService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a package product by ID", description = "Deletes a package product based on the given ID.")
    public ResponseEntity<Void> deletePackage(@PathVariable String id) {
        return packageProductService.deletePackage(id) ?
                ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
