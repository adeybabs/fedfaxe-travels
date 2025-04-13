package com.project.fedfaxe.service;


import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;



@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public String uploadFile(String key, MultipartFile file) throws IOException {
        // Get the input stream from MultipartFile
        InputStream inputStream = file.getInputStream();

        // Prepare the content length (size of the file)
        long contentLength = file.getSize();

        // Create a PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType()) // Dynamically set content type
                .build();

        // Upload the file to S3
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

        // Return the file URL
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toString();
    }


    // Download a file from S3
    public ResponseInputStream<GetObjectResponse> getFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    // Delete a file from S3
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // You can add more methods for additional operations (e.g., list files, check if file exists, etc.)

}
