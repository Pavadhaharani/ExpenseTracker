package com.expensetracker.service;

import com.expensetracker.dto.response.DashboardSummaryResponse;
import com.expensetracker.dto.response.DashboardSummaryResponse.CategorySummary;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseService    expenseService;

    /**
     * Builds the full dashboard payload in a single read-only transaction:
     * totals, category breakdown with percentages, and recent transactions.
     */
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(UserPrincipal principal) {
        Long      userId = principal.getId();
        LocalDate now    = LocalDate.now();

        BigDecimal totalExpenses        = expenseRepository.sumTotalByUser(userId);
        BigDecimal currentMonthExpenses = expenseRepository.sumByUserAndMonth(
            userId, now.getYear(), now.getMonthValue());
        long totalTransactions = expenseRepository.countByUserId(userId);

        // Category breakdown — percentages are calculated against the grand total
        List<Object[]> rawBreakdown = expenseRepository.getCategoryBreakdown(
            userId, null, null);

        List<CategorySummary> breakdown = rawBreakdown.stream()
            .map(row -> {
                BigDecimal amount = (BigDecimal) row[3];
                double pct = totalExpenses.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                    amount.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                          .doubleValue();
                return CategorySummary.builder()
                    .categoryName((String) row[0])
                    .color((String) row[1])
                    .icon((String) row[2])
                    .totalAmount(amount)
                    .percentage(Math.round(pct * 100.0) / 100.0)  // 2 dp
                    .build();
            })
            .toList();

        // Recent 5 expenses mapped to the shared ExpenseResponse DTO
        var recentExpenses = expenseRepository
            .findTop5ByUserIdOrderByExpenseDateDescCreatedAtDesc(userId)
            .stream()
            .map(expenseService::toResponse)
            .toList();

        return DashboardSummaryResponse.builder()
            .totalExpenses(totalExpenses)
            .currentMonthExpenses(currentMonthExpenses)
            .totalTransactions(totalTransactions)
            .categoryBreakdown(breakdown)
            .recentTransactions(recentExpenses)
            .build();
    }
}
