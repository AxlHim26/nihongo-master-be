package com.example.japanweb.service.impl;

import com.example.japanweb.dto.response.vocab.BulkImportResultDTO;
import com.example.japanweb.entity.VocabCourse;
import com.example.japanweb.entity.VocabEntry;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.VocabCourseRepository;
import com.example.japanweb.repository.VocabEntryRepository;
import com.example.japanweb.service.BulkImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BulkImportService using Apache POI for Excel parsing.
 * 
 * Expected Excel format:
 * | Term | Reading | Meaning | Example | Level |
 * | 食べる | たべる | to eat | 私はご飯を食べる | N5 |
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BulkImportServiceImpl implements BulkImportService {

    private final VocabCourseRepository vocabCourseRepository;
    private final VocabEntryRepository vocabEntryRepository;

    private static final int COL_TERM = 0;
    private static final int COL_READING = 1;
    private static final int COL_MEANING = 2;
    private static final int COL_EXAMPLE = 3;
    private static final int COL_LEVEL = 4;

    @Override
    @Transactional
    public BulkImportResultDTO importVocabulary(MultipartFile file, Long courseId) {
        // Validate file
        if (file.isEmpty()) {
            throw new ApiException(ErrorCode.VOCAB_INVALID_EXCEL, "File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new ApiException(ErrorCode.VOCAB_INVALID_EXCEL,
                    "Invalid file format. Only .xlsx files are supported");
        }

        // Find the target course
        VocabCourse course = vocabCourseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.VOCAB_COURSE_NOT_FOUND));

        List<String> errors = new ArrayList<>();
        List<VocabEntry> entriesToSave = new ArrayList<>();
        int totalRows = 0;

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new ApiException(ErrorCode.VOCAB_INVALID_EXCEL, "No sheet found in workbook");
            }

            // Skip header row
            int rowStart = sheet.getFirstRowNum() + 1;
            int rowEnd = sheet.getLastRowNum();
            totalRows = rowEnd - rowStart + 1;

            log.info("Processing {} rows from Excel file for course {}", totalRows, courseId);

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }

                try {
                    VocabEntry entry = parseRow(row, course, rowNum);
                    if (entry != null) {
                        entriesToSave.add(entry);
                    }
                } catch (Exception e) {
                    String errorMsg = String.format("Row %d: %s", rowNum + 1, e.getMessage());
                    errors.add(errorMsg);
                    log.warn("Error parsing row {}: {}", rowNum + 1, e.getMessage());
                }
            }

            // Batch save all valid entries
            if (!entriesToSave.isEmpty()) {
                vocabEntryRepository.saveAll(entriesToSave);

                // Update course total word count
                int newTotal = course.getTotalWords() == null ? entriesToSave.size()
                        : course.getTotalWords() + entriesToSave.size();
                course.setTotalWords(newTotal);
                vocabCourseRepository.save(course);

                log.info("Successfully imported {} vocabulary entries to course {}",
                        entriesToSave.size(), courseId);
            }

        } catch (IOException e) {
            log.error("Failed to read Excel file", e);
            throw new ApiException(ErrorCode.VOCAB_IMPORT_FAILED,
                    "Failed to read Excel file: " + e.getMessage());
        }

        return BulkImportResultDTO.builder()
                .totalRows(totalRows)
                .successCount(entriesToSave.size())
                .errorCount(errors.size())
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    /**
     * Parse a single row from the Excel sheet into a VocabEntry.
     */
    private VocabEntry parseRow(Row row, VocabCourse course, int rowNum) {
        String term = getCellStringValue(row.getCell(COL_TERM));
        String reading = getCellStringValue(row.getCell(COL_READING));
        String meaning = getCellStringValue(row.getCell(COL_MEANING));
        String example = getCellStringValue(row.getCell(COL_EXAMPLE));
        String level = getCellStringValue(row.getCell(COL_LEVEL));

        // Validate required fields
        if (term == null || term.isBlank()) {
            throw new IllegalArgumentException("Term is required");
        }
        if (reading == null || reading.isBlank()) {
            throw new IllegalArgumentException("Reading is required");
        }
        if (meaning == null || meaning.isBlank()) {
            throw new IllegalArgumentException("Meaning is required");
        }

        return VocabEntry.builder()
                .course(course)
                .term(term.trim())
                .reading(reading.trim())
                .meaning(meaning.trim())
                .example(example != null ? example.trim() : null)
                .level(level != null ? level.trim() : null)
                .build();
    }

    /**
     * Get string value from a cell, handling different cell types.
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }
}
