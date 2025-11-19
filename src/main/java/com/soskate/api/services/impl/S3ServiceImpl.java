package com.soskate.api.services.impl;


import com.soskate.api.exceptions.photo.PhotoUploadException;
import com.soskate.api.services.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.UUID;

/**
 * Implementation of S3Service using AWS SDK v2.
 */
@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public S3ServiceImpl(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType, long fileSize) {
        try {
            log.info("Uploading file to S3: bucket={}, key={}, size={}", bucketName, fileName, fileSize);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength(fileSize)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));

            // Generate public URL
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);

            log.info("File uploaded successfully: {}", url);
            return url;

        } catch (S3Exception e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new PhotoUploadException("Échec de l'upload sur S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 upload: {}", e.getMessage(), e);
            throw new PhotoUploadException("Erreur inattendue lors de l'upload: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            log.info("Deleting file from S3: bucket={}, key={}", bucketName, fileKey);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("File deleted successfully: {}", fileKey);

        } catch (S3Exception e) {
            log.error("Failed to delete file from S3: {}", e.getMessage(), e);
            throw new PhotoUploadException("Échec de la suppression sur S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String fileKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking file existence: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String generateUniqueFileName(String originalFileName, String prefix) {
        // Extract extension from original file name
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Generate unique name: timestamp_uuid.ext
        String uniqueName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString();

        // Combine prefix + unique name + extension
        return prefix + "/" + uniqueName + extension;
    }
}