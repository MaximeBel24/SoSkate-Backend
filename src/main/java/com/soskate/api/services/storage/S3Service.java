package com.soskate.api.services.storage;

import java.io.InputStream;

/**
 * Service for AWS S3 operations.
 * Handles file upload, download, and deletion from S3 bucket.
 */
public interface S3Service {

    /**
     * Upload a file to S3 and return the public URL.
     *
     * @param inputStream File content as InputStream
     * @param fileName    Desired file name (with path, e.g., "spots/full/123_456789.webp")
     * @param contentType MIME type (e.g., "image/webp")
     * @param fileSize    File size in bytes
     * @return Public URL of the uploaded file
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType, long fileSize);

    /**
     * Delete a file from S3.
     *
     * @param fileKey S3 key (path) of the file to delete (e.g., "spots/full/123.webp")
     */
    void deleteFile(String fileKey);

    /**
     * Check if a file exists in S3.
     *
     * @param fileKey S3 key of the file
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String fileKey);

    /**
     * Generate a unique file name with timestamp and UUID.
     *
     * @param originalFileName Original file name
     * @param prefix           Path prefix (e.g., "spots/full")
     * @return Unique file name (e.g., "spots/full/1234567890_abc-def.webp")
     */
    String generateUniqueFileName(String originalFileName, String prefix);
}