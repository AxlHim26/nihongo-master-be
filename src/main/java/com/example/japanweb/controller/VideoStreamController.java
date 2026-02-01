package com.example.japanweb.controller;

import com.example.japanweb.entity.LessonVideo;
import com.example.japanweb.service.GoogleDriveService;
import com.example.japanweb.service.VideoStreamingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * REST Controller for video streaming operations.
 * Streams video files from Google Drive with HTTP Range support for seeking.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoStreamController {

    private static final int BUFFER_SIZE = 8192; // 8KB buffer for streaming

    private final VideoStreamingService videoStreamingService;

    /**
     * Stream a video file from Google Drive.
     * Supports HTTP Range requests for video seeking.
     *
     * <p>
     * Request headers:
     * <ul>
     * <li>Authorization: Bearer {token} (required)</li>
     * <li>Range: bytes=0-1048575 (optional, for partial content)</li>
     * </ul>
     *
     * <p>
     * Response headers:
     * <ul>
     * <li>Content-Type: video/mp4 (or actual MIME type)</li>
     * <li>Content-Length: size of content being sent</li>
     * <li>Content-Range: bytes start-end/total (for partial content)</li>
     * <li>Accept-Ranges: bytes</li>
     * </ul>
     *
     * @param id       Video ID from database
     * @param request  HTTP request to read Range header
     * @param response HTTP response to write video bytes
     */
    @GetMapping("/{id}/stream")
    public void streamVideo(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        log.info("Video stream request for ID: {}", id);

        // 1. Get video metadata from database
        LessonVideo video = videoStreamingService.getVideoById(id);
        String driveFileId = video.getDriveFileId();

        // 2. Get file metadata from Google Drive
        GoogleDriveService.FileMetadata metadata = videoStreamingService.getVideoMetadata(driveFileId);
        long fileSize = metadata.size();
        String mimeType = metadata.mimeType() != null ? metadata.mimeType() : "video/mp4";

        // 3. Parse Range header
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        VideoStreamingService.RangeInfo rangeInfo = videoStreamingService.parseRangeHeader(rangeHeader, fileSize);

        // 4. Set response headers
        response.setContentType(mimeType);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

        if (rangeHeader != null) {
            // Partial content response (206)
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            response.setHeader(HttpHeaders.CONTENT_RANGE,
                    String.format("bytes %d-%d/%d", rangeInfo.start(), rangeInfo.end(), fileSize));
            response.setContentLengthLong(rangeInfo.length());

            log.debug("Streaming partial content: bytes {}-{}/{}",
                    rangeInfo.start(), rangeInfo.end(), fileSize);
        } else {
            // Full content response (200)
            response.setStatus(HttpStatus.OK.value());
            response.setContentLengthLong(fileSize);

            log.debug("Streaming full content: {} bytes", fileSize);
        }

        // 5. Stream video content directly from Google Drive to client
        try (InputStream inputStream = videoStreamingService.streamVideo(
                driveFileId, rangeInfo.start(), rangeInfo.end());
                OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesWritten = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesWritten += bytesRead;
            }

            outputStream.flush();
            log.debug("Streamed {} bytes for video ID: {}", totalBytesWritten, id);

        } catch (IOException e) {
            // Client may have disconnected, which is normal for video streaming
            if (response.isCommitted()) {
                log.debug("Client disconnected during streaming for video ID: {}", id);
            } else {
                log.error("Error streaming video ID: {}", id, e);
                throw e;
            }
        }
    }
}
