package com.project.fedfaxe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final S3Service s3Service;

    @Autowired
    public ImageUploadService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public String uploadProductImage(MultipartFile file, String productType) throws IOException {
        // Generate a unique key for the file with product type prefix for better organization
        String key = productType + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Upload to S3 and return the URL
        return s3Service.uploadFile(key, file);
    }

    public List<String> uploadProductImages(List<MultipartFile> files, String productType) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String key = productType + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            String url = s3Service.uploadFile(key, file);
            urls.add(url);
        }

        return urls;
    }
}
