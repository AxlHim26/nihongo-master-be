package com.example.japanweb.service;

import com.example.japanweb.entity.LessonVideo;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.LessonVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Service for orchestrating video streaming operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamingService {

    private final LessonVideoRepository videoRepository;
    private final GoogleDriveService googleDriveService;

    /**
     * Get video entity by ID.
     *
     * @param videoId Video ID from database
     * @return LessonVideo entity
     * @throws ApiException if video not found
     */
    public LessonVideo getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ApiException(ErrorCode.VIDEO_NOT_FOUND,
                        "Video with ID " + videoId + " not found"));
    }

    /**
     * Get file metadata from Google Drive.
     *
     * @param driveFileId Google Drive file ID
     * @return File metadata (size, MIME type, etc.)
     */
    public GoogleDriveService.FileMetadata getVideoMetadata(String driveFileId) {
        return googleDriveService.getFileMetadata(driveFileId);
    }

    /**
     * Stream video content from Google Drive with optional byte range.
     *
     * @param driveFileId Google Drive file ID
     * @param rangeStart  Start byte (0-indexed), null for beginning
     * @param rangeEnd    End byte (inclusive), null for end of file
     * @return InputStream for streaming to client
     */
    public InputStream streamVideo(String driveFileId, Long rangeStart, Long rangeEnd) {
        log.debug("Streaming video {} with range {}-{}", driveFileId, rangeStart, rangeEnd);
        return googleDriveService.streamFile(driveFileId, rangeStart, rangeEnd);
    }

    /**
     * Parse HTTP Range header and validate against file size.
     *
     * @param rangeHeader HTTP Range header value (e.g., "bytes=0-1048575")
     * @param fileSize    Total file size
     * @return RangeInfo containing parsed range values
     */
    public RangeInfo parseRangeHeader(String rangeHeader, long fileSize) {
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            // No range specified, return full file
            return new RangeInfo(0, fileSize - 1, fileSize);
        }

        try {
            String rangeValue = rangeHeader.substring(6); // Remove "bytes="
            String[] parts = rangeValue.split("-");

            long rangeStart;
            long rangeEnd;

            if (parts[0].isEmpty()) {
                // Suffix range: bytes=-500 (last 500 bytes)
                long suffixLength = Long.parseLong(parts[1]);
                rangeStart = Math.max(0, fileSize - suffixLength);
                rangeEnd = fileSize - 1;
            } else if (parts.length == 1 || parts[1].isEmpty()) {
                // Open-ended range: bytes=500- (from 500 to end)
                rangeStart = Long.parseLong(parts[0]);
                rangeEnd = fileSize - 1;
            } else {
                // Standard range: bytes=0-1048575
                rangeStart = Long.parseLong(parts[0]);
                rangeEnd = Long.parseLong(parts[1]);
            }

            // Validate range
            if (rangeStart < 0 || rangeStart >= fileSize || rangeEnd < rangeStart) {
                throw new ApiException(ErrorCode.VIDEO_INVALID_RANGE,
                        "Invalid range: " + rangeHeader + " for file size: " + fileSize);
            }

            // Cap rangeEnd to file size
            rangeEnd = Math.min(rangeEnd, fileSize - 1);

            return new RangeInfo(rangeStart, rangeEnd, rangeEnd - rangeStart + 1);
        } catch (NumberFormatException e) {
            throw new ApiException(ErrorCode.VIDEO_INVALID_RANGE,
                    "Malformed Range header: " + rangeHeader);
        }
    }

    /**
     * Record containing parsed range information.
     */
    public record RangeInfo(
            long start,
            long end,
            long length) {
    }
}
