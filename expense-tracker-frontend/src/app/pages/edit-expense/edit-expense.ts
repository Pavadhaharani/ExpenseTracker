import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Category } from '../../core/models/category.models';
import { Expense, ExpenseRequest } from '../../core/models/expense.models';
import { CategoryService } from '../../core/services/category.service';
import { ExpenseService } from '../../core/services/expense.service';
import { NotificationService } from '../../core/services/notification.service';
import { ExpenseForm } from '../../components/expense-form/expense-form';

@Component({
  selector: 'app-edit-expense',
  imports: [ExpenseForm],
  templateUrl: './edit-expense.html'
})
export class EditExpensePage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly categoryService = inject(CategoryService);
  private readonly expenseService = inject(ExpenseService);
  private readonly notifications = inject(NotificationService);

  readonly categories = signal<Category[]>([]);
  readonly expense = signal<Expense | null>(null);
  private expenseId = 0;

  ngOnInit(): void {
    this.expenseId = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      categories: this.categoryService.getCategories(),
      expense: this.expenseService.getExpense(this.expenseId)
    }).subscribe(({ categories, expense }) => {
      this.categories.set(categories);
      this.expense.set(expense);
    });
  }

  save(payload: ExpenseRequest): void {
    this.expenseService.updateExpense(this.expenseId, payload).subscribe(() => {
      this.notifications.success('Expense updated');
      this.router.navigateByUrl('/expenses');
    });
  }
}
