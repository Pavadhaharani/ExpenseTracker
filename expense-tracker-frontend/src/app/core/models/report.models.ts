export interface MonthlyEntry {
  month: number;
  monthName: string;
  total: number;
}

export interface MonthlyReport {
  year: number;
  months: MonthlyEntry[];
  yearTotal: number;
}

export interface CategoryEntry {
  categoryName: string;
  color?: string;
  icon?: string;
  total: number;
  percentage: number;
}

export interface CategoryReport {
  startDate?: string;
  endDate?: string;
  grandTotal: number;
  categories: CategoryEntry[];
}
