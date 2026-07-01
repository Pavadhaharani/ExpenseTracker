package com.expensetracker.controller;

import com.expensetracker.dto.response.ApiResponse;
import com.expensetracker.dto.response.CategoryReportResponse;
import com.expensetracker.dto.response.MonthlyReportResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Report endpoints.
 *
 * <ul>
 *   <li>GET /api/reports/monthly?year=YYYY — monthly totals for a year</li>
 *   <li>GET /api/reports/category?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd — category breakdown</li>
 * </ul>
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Monthly expense report.
     * Defaults to the current year when {@code year} is not provided.
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer year) {

        int reportYear = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(
            ApiResponse.success(reportService.getMonthlyReport(principal, reportYear)));
    }

    /**
     * Category expense report for an optional date range.
     * Omitting both dates returns the all-time category breakdown.
     */
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<CategoryReportResponse>> getCategoryReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
            ApiResponse.success(
                reportService.getCategoryReport(principal, startDate, endDate)));
    }
}
