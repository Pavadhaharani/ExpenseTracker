package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** Returned by {@code GET /api/reports/monthly?year=YYYY}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportResponse {

    private int                year;
    private List<MonthlyEntry> months;

    /** Sum of all monthly totals — equal to the user's total spend for the year. */
    private BigDecimal yearTotal;

    // ── Nested DTO ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyEntry {
        /** Calendar month number (1–12). */
        private int        month;
        /** Full month name, e.g. "January". */
        private String     monthName;
        /** Total expenses for this month (0 if no expenses). */
        private BigDecimal total;
    }
}
