package com.project.fedfaxe.controller;

import com.project.fedfaxe.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/image")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class ImageUploadController {

    private final ImageUploadService imageUploadService;



    @Operation(summary = "Add a new image to product", description = "Creates a url for image added during product creation by Admin.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("productType") String productType) throws IOException {

        // Call service to upload the image
        String imageUrl = imageUploadService.uploadProductImage(image, productType);

        // Return the URL in a response
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Upload multiple product images", description = "Uploads multiple images and returns a list of URLs.")
    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, List<String>>> uploadMultipleProductImages(
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("productType") String productType) throws IOException {

        List<String> imageUrls = imageUploadService.uploadProductImages(images, productType);

        Map<String, List<String>> response = new HashMap<>();
        response.put("imageUrls", imageUrls);

        return ResponseEntity.ok(response);
    }



}

