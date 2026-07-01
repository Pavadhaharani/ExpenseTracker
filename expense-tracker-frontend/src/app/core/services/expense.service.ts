import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PagedResponse } from '../models/api.models';
import { Expense, ExpenseFilters, ExpenseRequest } from '../models/expense.models';

@Injectable({ providedIn: 'root' })
export class ExpenseService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getExpenses(filters: ExpenseFilters = {}): Observable<PagedResponse<Expense>> {
    let params = new HttpParams()
      .set('page', String(filters.page ?? 0))
      .set('size', String(filters.size ?? 10));

    if (filters.keyword) params = params.set('keyword', filters.keyword);
    if (filters.categoryId) params = params.set('categoryId', String(filters.categoryId));
    if (filters.startDate) params = params.set('startDate', filters.startDate);
    if (filters.endDate) params = params.set('endDate', filters.endDate);

    return this.http
      .get<ApiResponse<PagedResponse<Expense>>>(`${this.apiUrl}/expenses`, { params })
      .pipe(map((response) => response.data));
  }

  getExpense(id: number): Observable<Expense> {
    return this.http
      .get<ApiResponse<Expense>>(`${this.apiUrl}/expenses/${id}`)
      .pipe(map((response) => response.data));
  }

  createExpense(payload: ExpenseRequest): Observable<Expense> {
    return this.http
      .post<ApiResponse<Expense>>(`${this.apiUrl}/expenses`, payload)
      .pipe(map((response) => response.data));
  }

  updateExpense(id: number, payload: ExpenseRequest): Observable<Expense> {
    return this.http
      .put<ApiResponse<Expense>>(`${this.apiUrl}/expenses/${id}`, payload)
      .pipe(map((response) => response.data));
  }

  deleteExpense(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.apiUrl}/expenses/${id}`)
      .pipe(map((response) => response.data));
  }
}
