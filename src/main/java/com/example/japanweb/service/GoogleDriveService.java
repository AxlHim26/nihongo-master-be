package com.example.japanweb.service;

import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Service for interacting with Google Drive API.
 * Uses Service Account authentication for backend-to-backend communication.
 */
@Slf4j
@Service
public class GoogleDriveService {

    @Value("${google.drive.application-name:Japience}")
    private String applicationName;

    @Value("${google.drive.credentials-path:#{null}}")
    private Resource credentialsResource;

    private Drive driveService;

    @PostConstruct
    public void init() {
        try {
            GoogleCredentials credentials = loadCredentials();
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            driveService = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName(applicationName)
                    .build();

            log.info("Google Drive service initialized successfully");
        } catch (GeneralSecurityException | IOException e) {
            log.warn("Failed to initialize Google Drive service: {}. Video streaming will not work.", e.getMessage());
            // Don't throw - allow app to start, but streaming will fail gracefully
        }
    }

    private GoogleCredentials loadCredentials() throws IOException {
        GoogleCredentials credentials;

        if (credentialsResource != null && credentialsResource.exists()) {
            // Load from specified credentials file
            try (InputStream inputStream = credentialsResource.getInputStream()) {
                credentials = GoogleCredentials.fromStream(inputStream)
                        .createScoped(Collections.singleton(DriveScopes.DRIVE_READONLY));
            }
            log.info("Loaded Google credentials from: {}", credentialsResource.getDescription());
        } else {
            // Fall back to Application Default Credentials (ADC)
            credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(Collections.singleton(DriveScopes.DRIVE_READONLY));
            log.info("Using Application Default Credentials for Google Drive");
        }

        return credentials;
    }

    /**
     * Get file metadata from Google Drive.
     *
     * @param fileId Google Drive file ID
     * @return File metadata containing size and MIME type
     */
    public FileMetadata getFileMetadata(String fileId) {
        ensureDriveServiceInitialized();

        try {
            File file = driveService.files().get(fileId)
                    .setFields("id, name, size, mimeType")
                    .execute();

            return new FileMetadata(
                    file.getId(),
                    file.getName(),
                    file.getSize(),
                    file.getMimeType());
        } catch (IOException e) {
            log.error("Failed to get file metadata for fileId: {}", fileId, e);
            throw new ApiException(ErrorCode.VIDEO_STREAM_FAILED,
                    "Failed to retrieve video metadata from Google Drive");
        }
    }

    /**
     * Stream file content from Google Drive with optional range support.
     * Returns an InputStream that can be used for streaming.
     *
     * @param fileId     Google Drive file ID
     * @param rangeStart Start byte position (0-indexed), or null for full file
     * @param rangeEnd   End byte position (inclusive), or null for full file
     * @return InputStream for the file content
     */
    public InputStream streamFile(String fileId, Long rangeStart, Long rangeEnd) {
        ensureDriveServiceInitialized();

        try {
            Drive.Files.Get request = driveService.files().get(fileId);

            // Set Range header if specified
            if (rangeStart != null) {
                String rangeHeader = rangeEnd != null
                        ? String.format("bytes=%d-%d", rangeStart, rangeEnd)
                        : String.format("bytes=%d-", rangeStart);
                request.getRequestHeaders().setRange(rangeHeader);
            }

            return request.executeMediaAsInputStream();
        } catch (IOException e) {
            log.error("Failed to stream file from Google Drive. FileId: {}, Range: {}-{}",
                    fileId, rangeStart, rangeEnd, e);
            throw new ApiException(ErrorCode.VIDEO_STREAM_FAILED,
                    "Failed to stream video from Google Drive");
        }
    }

    private void ensureDriveServiceInitialized() {
        if (driveService == null) {
            throw new ApiException(ErrorCode.SERVICE_UNAVAILABLE,
                    "Google Drive service is not available");
        }
    }

    /**
     * Record containing file metadata from Google Drive.
     */
    public record FileMetadata(
            String id,
            String name,
            Long size,
            String mimeType) {
    }
}
