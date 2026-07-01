import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MonthlyReport } from '../../core/models/report.models';
import { ReportService } from '../../core/services/report.service';
import { BarChart, BarChartItem } from '../../components/bar-chart/bar-chart';

@Component({
  selector: 'app-monthly-report',
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    BarChart
  ],
  templateUrl: './monthly-report.html',
  styleUrl: './report-page.scss'
})
export class MonthlyReportPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly reportService = inject(ReportService);

  readonly report = signal<MonthlyReport | null>(null);
  readonly form = this.fb.nonNullable.group({
    year: [new Date().getFullYear()]
  });

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.reportService.getMonthlyReport(Number(this.form.controls.year.value)).subscribe((report) => {
      this.report.set(report);
    });
  }

  chartItems(report: MonthlyReport): BarChartItem[] {
    return report.months.map((entry) => ({
      label: entry.monthName.slice(0, 3),
      value: Number(entry.total)
    }));
  }
}
