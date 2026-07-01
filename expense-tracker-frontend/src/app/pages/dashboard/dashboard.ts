import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { DashboardSummary } from '../../core/models/dashboard.models';
import { DashboardService } from '../../core/services/dashboard.service';
import { DonutChart, DonutChartItem } from '../../components/donut-chart/donut-chart';

@Component({
  selector: 'app-dashboard',
  imports: [
    CurrencyPipe,
    DatePipe,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    DonutChart
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardPage implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  readonly summary = signal<DashboardSummary | null>(null);
  readonly displayedColumns = ['date', 'title', 'category', 'amount'];

  ngOnInit(): void {
    this.dashboardService.getSummary().subscribe((summary) => this.summary.set(summary));
  }

  chartItems(summary: DashboardSummary): DonutChartItem[] {
    return summary.categoryBreakdown.map((item) => ({
      label: item.categoryName,
      value: Number(item.totalAmount),
      percentage: item.percentage,
      color: item.color
    }));
  }
}
