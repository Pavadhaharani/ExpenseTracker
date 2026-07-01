import { Expense } from './expense.models';

export interface CategorySummary {
  categoryName: string;
  color?: string;
  icon?: string;
  totalAmount: number;
  percentage: number;
}

export interface DashboardSummary {
  totalExpenses: number;
  currentMonthExpenses: number;
  totalTransactions: number;
  categoryBreakdown: CategorySummary[];
  recentTransactions: Expense[];
}
