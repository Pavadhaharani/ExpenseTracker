package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Expense}.
 *
 * <p>All queries are scoped to a specific {@code userId} to enforce
 * ownership — a user can never read or mutate another user's expenses.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Fetches one expense visible to the given user.
     * Returns empty when the expense belongs to a different user, which
     * the service layer converts to a 404 (not a 403) to avoid leaking
     * information about other users' expense IDs.
     */
    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    /**
     * Full-text search across title and description with optional filters
     * for category, date range, and pagination.
     *
     * <p>Null parameters are treated as "no filter" via JPQL's IS NULL check,
     * which lets the query serve both the basic list view and the search view
     * from the same method.
     */
    @Query("""
            SELECT e FROM Expense e
             WHERE e.user.id = :userId
               AND (:keyword    IS NULL OR :keyword = ''
                    OR LOWER(e.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
               AND (:categoryId IS NULL OR e.category.id = :categoryId)
               AND (:startDate  IS NULL OR e.expenseDate >= :startDate)
               AND (:endDate    IS NULL OR e.expenseDate <= :endDate)
             ORDER BY e.expenseDate DESC, e.createdAt DESC
           """)
    Page<Expense> searchExpenses(
            @Param("userId")     Long      userId,
            @Param("keyword")    String    keyword,
            @Param("categoryId") Integer   categoryId,
            @Param("startDate")  LocalDate startDate,
            @Param("endDate")    LocalDate endDate,
            Pageable             pageable);

    /** Total count of expenses for a user — used on the dashboard. */
    long countByUserId(Long userId);

    /** Grand total amount spent by a user across all time. */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal sumTotalByUser(@Param("userId") Long userId);

    /** Total amount spent by a user in a specific calendar month. */
    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
              FROM Expense e
             WHERE e.user.id          = :userId
               AND YEAR(e.expenseDate)  = :year
               AND MONTH(e.expenseDate) = :month
           """)
    BigDecimal sumByUserAndMonth(
            @Param("userId") Long userId,
            @Param("year")   int  year,
            @Param("month")  int  month);

    /**
     * Category breakdown for a date range.
     * Returns rows of {@code [categoryName, color, icon, totalAmount]}.
     * Used by both the dashboard pie chart and the category report.
     */
    @Query("""
            SELECT e.category.name,
                   e.category.color,
                   e.category.icon,
                   SUM(e.amount)
              FROM Expense e
             WHERE e.user.id = :userId
               AND (:startDate IS NULL OR e.expenseDate >= :startDate)
               AND (:endDate   IS NULL OR e.expenseDate <= :endDate)
             GROUP BY e.category.id,
                      e.category.name,
                      e.category.color,
                      e.category.icon
             ORDER BY SUM(e.amount) DESC
           """)
    List<Object[]> getCategoryBreakdown(
            @Param("userId")    Long      userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate);

    /**
     * Monthly totals for a given year.
     * Returns rows of {@code [year, month, totalAmount]}.
     */
    @Query("""
            SELECT YEAR(e.expenseDate),
                   MONTH(e.expenseDate),
                   SUM(e.amount)
              FROM Expense e
             WHERE e.user.id            = :userId
               AND YEAR(e.expenseDate)  = :year
             GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate)
             ORDER BY MONTH(e.expenseDate)
           """)
    List<Object[]> getMonthlyTotals(
            @Param("userId") Long userId,
            @Param("year")   int  year);

    /** Most recent 5 expenses — shown in the dashboard's Recent Transactions card. */
    List<Expense> findTop5ByUserIdOrderByExpenseDateDescCreatedAtDesc(Long userId);
}
