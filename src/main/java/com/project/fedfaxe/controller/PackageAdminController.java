package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.repository.PackageProductRepository;
import com.project.fedfaxe.service.PackageProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PackageProduct> addPackage(@RequestBody PackageProduct packageProduct) {
        return ResponseEntity.ok(packageProductService.addPackage(packageProduct));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a package product by ID", description = "Retrieves a package product based on the given ID.")
    public ResponseEntity<PackageProduct> getPackageById(@PathVariable String id) {
        Optional<PackageProduct> packageProduct = packageProductService.getPackageById(id);
        return packageProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a package product by ID", description = "Deletes a package product based on the given ID.")
    public ResponseEntity<Void> deletePackage(@PathVariable String id) {
        return packageProductService.deletePackage(id) ?
                ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
