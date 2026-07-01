package com.expensetracker.service;

import com.expensetracker.dto.response.CategoryReportResponse;
import com.expensetracker.dto.response.CategoryReportResponse.CategoryEntry;
import com.expensetracker.dto.response.MonthlyReportResponse;
import com.expensetracker.dto.response.MonthlyReportResponse.MonthlyEntry;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ExpenseRepository expenseRepository;

    /**
     * Monthly report for a given year.
     * Months with no expenses are included with {@code total = 0} so the
     * Angular chart always shows a full 12-bar dataset.
     */
    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(UserPrincipal principal, int year) {
        List<Object[]> rows = expenseRepository.getMonthlyTotals(principal.getId(), year);

        // Index the DB results by month number for O(1) lookup
        Map<Integer, BigDecimal> byMonth = rows.stream()
            .collect(Collectors.toMap(
                r -> ((Number) r[1]).intValue(),
                r -> (BigDecimal) r[2]));

        List<MonthlyEntry> entries  = new ArrayList<>(12);
        BigDecimal         yearTotal = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            BigDecimal total = byMonth.getOrDefault(m, BigDecimal.ZERO);
            yearTotal = yearTotal.add(total);
            String monthName = Month.of(m).name().charAt(0)
                + Month.of(m).name().substring(1).toLowerCase();
            entries.add(MonthlyEntry.builder()
                .month(m)
                .monthName(monthName)
                .total(total)
                .build());
        }

        return MonthlyReportResponse.builder()
            .year(year)
            .months(entries)
            .yearTotal(yearTotal)
            .build();
    }

    /**
     * Category breakdown for an optional date range.
     * {@code startDate} and {@code endDate} may be null, in which case the
     * report covers all time.
     */
    @Transactional(readOnly = true)
    public CategoryReportResponse getCategoryReport(UserPrincipal principal,
                                                     LocalDate startDate,
                                                     LocalDate endDate) {
        List<Object[]> rows = expenseRepository.getCategoryBreakdown(
            principal.getId(), startDate, endDate);

        BigDecimal grandTotal = rows.stream()
            .map(r -> (BigDecimal) r[3])
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoryEntry> entries = rows.stream()
            .map(row -> {
                BigDecimal total = (BigDecimal) row[3];
                double pct = grandTotal.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                    total.divide(grandTotal, 4, RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100))
                         .doubleValue();
                return CategoryEntry.builder()
                    .categoryName((String) row[0])
                    .color((String) row[1])
                    .icon((String) row[2])
                    .total(total)
                    .percentage(Math.round(pct * 100.0) / 100.0)
                    .build();
            })
            .toList();

        return CategoryReportResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .grandTotal(grandTotal)
            .categories(entries)
            .build();
    }
}
