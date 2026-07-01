import { Category } from './category.models';

export interface Expense {
  id: number;
  title: string;
  description?: string;
  amount: number;
  expenseDate: string;
  category: Category;
  createdAt: string;
  updatedAt: string;
}

export interface ExpenseRequest {
  title: string;
  description?: string;
  amount: number;
  expenseDate: string;
  categoryId: number;
}

export interface ExpenseFilters {
  page?: number;
  size?: number;
  keyword?: string;
  categoryId?: number | null;
  startDate?: string | null;
  endDate?: string | null;
}
