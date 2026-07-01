package com.expensetracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Core domain entity — represents a single expense record.
 *
 * <ul>
 *   <li>{@code user} is LAZY-fetched: we rarely need the full User object
 *       when listing expenses, so we avoid the extra join by default.</li>
 *   <li>{@code category} is EAGER-fetched: it's always displayed alongside
 *       the expense in both list and detail views.</li>
 *   <li>Composite indexes on {@code (user_id)}, {@code (expense_date)},
 *       {@code (user_id, expense_date)}, and {@code (category_id)} are
 *       declared here so they are created by Hibernate's DDL generation.</li>
 * </ul>
 */
@Entity
@Table(
    name = "expenses",
    indexes = {
        @Index(name = "idx_expenses_user_id",      columnList = "user_id"),
        @Index(name = "idx_expenses_expense_date", columnList = "expense_date"),
        @Index(name = "idx_expenses_user_date",    columnList = "user_id, expense_date"),
        @Index(name = "idx_expenses_category",     columnList = "category_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Owning user — LAZY because expense lists don't need user details. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Expense category — EAGER because it's always shown with the expense. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Monetary amount; precision 12, scale 2 supports values up to
     * 9,999,999,999.99 (sufficient for any personal expense).
     */
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** Calendar date on which the expense was incurred. */
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
