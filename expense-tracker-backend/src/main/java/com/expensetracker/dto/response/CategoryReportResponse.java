package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Returned by {@code GET /api/reports/category?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD}.
 * Both date params are optional; omitting them covers all time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryReportResponse {

    private LocalDate           startDate;
    private LocalDate           endDate;
    private BigDecimal          grandTotal;
    private List<CategoryEntry> categories;

    // ── Nested DTO ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryEntry {
        private String     categoryName;
        private String     color;
        private String     icon;
        private BigDecimal total;
        /** Share of the grand total, rounded to 2 decimal places. */
        private double     percentage;
    }
}
