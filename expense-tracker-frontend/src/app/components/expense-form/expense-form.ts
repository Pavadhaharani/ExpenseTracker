import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Category } from '../../core/models/category.models';
import { Expense, ExpenseRequest } from '../../core/models/expense.models';
import { fromIsoDate, toIsoDate } from '../../shared/utils/date.utils';

@Component({
  selector: 'app-expense-form',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './expense-form.html',
  styleUrl: './expense-form.scss'
})
export class ExpenseForm implements OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly wholeNumberPattern = /^\d+$/;

  @Input() expense: Expense | null = null;
  @Input() categories: Category[] = [];
  @Input() submitLabel = 'Save Expense';
  @Output() formSubmit = new EventEmitter<ExpenseRequest>();

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(1000)]],
    amount: ['', [Validators.required, Validators.pattern(this.wholeNumberPattern)]],
    expenseDate: [new Date(), [Validators.required]],
    categoryId: [0, [Validators.required, Validators.min(1)]]
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['expense'] && this.expense) {
      this.form.patchValue({
        title: this.expense.title,
        description: this.expense.description || '',
        amount: String(Math.trunc(Number(this.expense.amount))),
        expenseDate: fromIsoDate(this.expense.expenseDate) ?? new Date(),
        categoryId: this.expense.category.id
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.formSubmit.emit({
      title: value.title.trim(),
      description: value.description?.trim() || undefined,
      amount: Number(value.amount),
      expenseDate: toIsoDate(value.expenseDate) ?? '',
      categoryId: Number(value.categoryId)
    });
  }

  sanitizeAmountInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D+/g, '');
    if (input.value !== digitsOnly) {
      input.value = digitsOnly;
    }
    this.form.controls.amount.setValue(digitsOnly);
  }

  blockNonNumericInput(event: KeyboardEvent): void {
    const allowedKeys = [
      'Backspace',
      'Delete',
      'Tab',
      'ArrowLeft',
      'ArrowRight',
      'Home',
      'End'
    ];

    if (allowedKeys.includes(event.key) || event.ctrlKey || event.metaKey) {
      return;
    }

    if (!/^\d$/.test(event.key)) {
      event.preventDefault();
    }
  }
}
