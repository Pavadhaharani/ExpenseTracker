import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { CategoryReport, MonthlyReport } from '../models/report.models';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getMonthlyReport(year: number): Observable<MonthlyReport> {
    const params = new HttpParams().set('year', String(year));
    return this.http
      .get<ApiResponse<MonthlyReport>>(`${this.apiUrl}/reports/monthly`, { params })
      .pipe(map((response) => response.data));
  }

  getCategoryReport(startDate?: string | null, endDate?: string | null): Observable<CategoryReport> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http
      .get<ApiResponse<CategoryReport>>(`${this.apiUrl}/reports/category`, { params })
      .pipe(map((response) => response.data));
  }
}
