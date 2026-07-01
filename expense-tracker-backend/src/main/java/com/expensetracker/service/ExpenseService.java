package com.expensetracker.service;

import com.expensetracker.dto.request.ExpenseRequest;
import com.expensetracker.dto.response.CategoryResponse;
import com.expensetracker.dto.response.ExpenseResponse;
import com.expensetracker.dto.response.PagedResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository  expenseRepository;
    private final UserRepository     userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Paginated, filterable list of expenses for the authenticated user.
     * All filter parameters are optional — null values are treated as
     * "no filter" by the JPQL query in {@code ExpenseRepository}.
     */
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getExpenses(UserPrincipal principal,
                                                       int page, int size,
                                                       String keyword,
                                                       Integer categoryId,
                                                       LocalDate startDate,
                                                       LocalDate endDate) {
        Page<Expense> result = expenseRepository.searchExpenses(
            principal.getId(), keyword, categoryId, startDate, endDate,
            PageRequest.of(page, size));

        return PagedResponse.<ExpenseResponse>builder()
            .content(result.getContent().stream().map(this::toResponse).toList())
            .page(result.getNumber())
            .size(result.getSize())
            .totalElements(result.getTotalElements())
            .totalPages(result.getTotalPages())
            .first(result.isFirst())
            .last(result.isLast())
            .build();
    }

    /** Fetches one expense. Returns 404 if not found or owned by another user. */
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id, UserPrincipal principal) {
        return toResponse(findOwned(id, principal.getId()));
    }

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request, UserPrincipal principal) {
        User     user     = getUser(principal.getId());
        Category category = getCategory(request.getCategoryId());

        Expense expense = Expense.builder()
            .user(user)
            .category(category)
            .title(request.getTitle())
            .description(request.getDescription())
            .amount(request.getAmount())
            .expenseDate(request.getExpenseDate())
            .build();

        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request,
                                          UserPrincipal principal) {
        Expense  expense  = findOwned(id, principal.getId());
        Category category = getCategory(request.getCategoryId());

        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(category);

        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(Long id, UserPrincipal principal) {
        expenseRepository.delete(findOwned(id, principal.getId()));
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /**
     * Package-private so that {@code DashboardService} can reuse it
     * without duplicating the mapping logic.
     */
    ExpenseResponse toResponse(Expense e) {
        return ExpenseResponse.builder()
            .id(e.getId())
            .title(e.getTitle())
            .description(e.getDescription())
            .amount(e.getAmount())
            .expenseDate(e.getExpenseDate())
            .category(CategoryResponse.builder()
                .id(e.getCategory().getId())
                .name(e.getCategory().getName())
                .icon(e.getCategory().getIcon())
                .color(e.getCategory().getColor())
                .isDefault(e.getCategory().getIsDefault())
                .build())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    private Expense findOwned(Long expenseId, Long userId) {
        return expenseRepository.findByIdAndUserId(expenseId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Category getCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }
}
