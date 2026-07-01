import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CategoryReport } from '../../core/models/report.models';
import { ReportService } from '../../core/services/report.service';
import { DonutChart, DonutChartItem } from '../../components/donut-chart/donut-chart';
import { toIsoDate } from '../../shared/utils/date.utils';

@Component({
  selector: 'app-category-report',
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    DonutChart
  ],
  templateUrl: './category-report.html',
  styleUrl: './report-page.scss'
})
export class CategoryReportPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly reportService = inject(ReportService);

  readonly report = signal<CategoryReport | null>(null);
  readonly form = this.fb.group({
    startDate: [null as Date | null],
    endDate: [null as Date | null]
  });

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    const value = this.form.getRawValue();
    this.reportService
      .getCategoryReport(toIsoDate(value.startDate), toIsoDate(value.endDate))
      .subscribe((report) => this.report.set(report));
  }

  reset(): void {
    this.form.reset({ startDate: null, endDate: null });
    this.loadReport();
  }

  chartItems(report: CategoryReport): DonutChartItem[] {
    return report.categories.map((entry) => ({
      label: entry.categoryName,
      value: Number(entry.total),
      percentage: entry.percentage,
      color: entry.color
    }));
  }
}
