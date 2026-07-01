package com.expensetracker.controller;

import com.expensetracker.dto.request.ExpenseRequest;
import com.expensetracker.dto.response.ApiResponse;
import com.expensetracker.dto.response.ExpenseResponse;
import com.expensetracker.dto.response.PagedResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * CRUD endpoints for expense management.
 *
 * <p>Every operation is scoped to the authenticated user — the service
 * layer enforces ownership via {@code findByIdAndUserId}.
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * GET /api/expenses
     * Paginated and filterable expense list.
     *
     * @param page       0-based page number (default 0)
     * @param size       Page size (default 10)
     * @param keyword    Optional full-text search in title / description
     * @param categoryId Optional category filter
     * @param startDate  Optional lower bound for expense date (ISO format: yyyy-MM-dd)
     * @param endDate    Optional upper bound for expense date (ISO format: yyyy-MM-dd)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getExpenses(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0")  int     page,
            @RequestParam(defaultValue = "10") int     size,
            @RequestParam(required = false)    String  keyword,
            @RequestParam(required = false)    Integer categoryId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(ApiResponse.success(
            expenseService.getExpenses(
                principal, page, size, keyword, categoryId, startDate, endDate)));
    }

    /** GET /api/expenses/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
            ApiResponse.success(expenseService.getExpenseById(id, principal)));
    }

    /** POST /api/expenses */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ExpenseResponse response = expenseService.createExpense(request, principal);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Expense created successfully", response));
    }

    /** PUT /api/expenses/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
            "Expense updated successfully",
            expenseService.updateExpense(id, request, principal)));
    }

    /** DELETE /api/expenses/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        expenseService.deleteExpense(id, principal);
        return ResponseEntity.ok(
            ApiResponse.success("Expense deleted successfully", null));
    }
}
