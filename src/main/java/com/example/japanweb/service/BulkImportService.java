package com.example.japanweb.service;

import com.example.japanweb.dto.BulkImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for bulk importing vocabulary from Excel files.
 */
public interface BulkImportService {

    /**
     * Import vocabulary entries from an Excel file.
     * 
     * Expected columns: Term, Reading, Meaning, Example, Level
     * 
     * @param file     The Excel file (.xlsx)
     * @param courseId Target course ID
     * @return Import result with success count and errors
     */
    BulkImportResultDTO importVocabulary(MultipartFile file, Long courseId);
}
