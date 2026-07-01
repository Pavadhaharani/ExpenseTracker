package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** Returned by {@code GET /api/dashboard/summary}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    /** Grand total of all expenses for the user (all time). */
    private BigDecimal totalExpenses;

    /** Total for the current calendar month. */
    private BigDecimal currentMonthExpenses;

    /** Total number of expense records. */
    private long totalTransactions;

    /** Per-category totals and percentages — drives the pie/doughnut chart. */
    private List<CategorySummary> categoryBreakdown;

    /** The 5 most recent transactions — shown in the Recent Transactions card. */
    private List<ExpenseResponse> recentTransactions;

    // ── Nested DTO ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private String     categoryName;
        private String     color;
        private String     icon;
        private BigDecimal totalAmount;
        /** Percentage of grand total, rounded to 2 decimal places. */
        private double     percentage;
    }
}
