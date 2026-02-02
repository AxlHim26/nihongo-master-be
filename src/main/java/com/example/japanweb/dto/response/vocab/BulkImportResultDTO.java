package com.example.japanweb.dto.response.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result DTO for bulk import operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResultDTO {
    private int totalRows;
    private int successCount;
    private int errorCount;
    private List<String> errors;
}
