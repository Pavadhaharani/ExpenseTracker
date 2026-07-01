import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { Category } from '../../core/models/category.models';
import { Expense } from '../../core/models/expense.models';
import { CategoryService } from '../../core/services/category.service';
import { ExpenseService } from '../../core/services/expense.service';
import { NotificationService } from '../../core/services/notification.service';
import { toIsoDate } from '../../shared/utils/date.utils';

@Component({
  selector: 'app-expenses-list',
  imports: [
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatSelectModule,
    MatTableModule
  ],
  templateUrl: './expenses-list.html',
  styleUrl: './expenses-list.scss'
})
export class ExpensesListPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly expenseService = inject(ExpenseService);
  private readonly categoryService = inject(CategoryService);
  private readonly notifications = inject(NotificationService);

  readonly categories = signal<Category[]>([]);
  readonly expenses = signal<Expense[]>([]);
  readonly totalElements = signal(0);
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);
  readonly displayedColumns = ['date', 'title', 'category', 'amount', 'actions'];

  readonly filters = this.fb.group({
    keyword: [''],
    categoryId: [null as number | null],
    startDate: [null as Date | null],
    endDate: [null as Date | null]
  });

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe((categories) => this.categories.set(categories));
    this.loadExpenses();
  }

  loadExpenses(): void {
    const filters = this.filters.getRawValue();
    this.expenseService
      .getExpenses({
        page: this.pageIndex(),
        size: this.pageSize(),
        keyword: filters.keyword || undefined,
        categoryId: filters.categoryId,
        startDate: toIsoDate(filters.startDate),
        endDate: toIsoDate(filters.endDate)
      })
      .subscribe((page) => {
        this.expenses.set(page.content);
        this.totalElements.set(page.totalElements);
      });
  }

  applyFilters(): void {
    this.pageIndex.set(0);
    this.loadExpenses();
  }

  resetFilters(): void {
    this.filters.reset({ keyword: '', categoryId: null, startDate: null, endDate: null });
    this.applyFilters();
  }

  pageChanged(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadExpenses();
  }

  deleteExpense(expense: Expense): void {
    if (!confirm(`Delete expense "${expense.title}"?`)) {
      return;
    }

    this.expenseService.deleteExpense(expense.id).subscribe(() => {
      this.notifications.success('Expense deleted');
      this.loadExpenses();
    });
  }
}
