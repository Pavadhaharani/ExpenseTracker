package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Returned by all expense endpoints. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long             id;
    private String           title;
    private String           description;
    private BigDecimal       amount;
    private LocalDate        expenseDate;
    private CategoryResponse category;
    private LocalDateTime    createdAt;
    private LocalDateTime    updatedAt;
}
