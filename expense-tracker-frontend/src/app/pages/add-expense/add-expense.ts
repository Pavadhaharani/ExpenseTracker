import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Category } from '../../core/models/category.models';
import { ExpenseRequest } from '../../core/models/expense.models';
import { CategoryService } from '../../core/services/category.service';
import { ExpenseService } from '../../core/services/expense.service';
import { NotificationService } from '../../core/services/notification.service';
import { ExpenseForm } from '../../components/expense-form/expense-form';

@Component({
  selector: 'app-add-expense',
  imports: [ExpenseForm],
  templateUrl: './add-expense.html'
})
export class AddExpensePage implements OnInit {
  private readonly categoryService = inject(CategoryService);
  private readonly expenseService = inject(ExpenseService);
  private readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  readonly categories = signal<Category[]>([]);

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe((categories) => this.categories.set(categories));
  }

  save(payload: ExpenseRequest): void {
    this.expenseService.createExpense(payload).subscribe(() => {
      this.notifications.success('Expense created');
      this.router.navigateByUrl('/expenses');
    });
  }
}
