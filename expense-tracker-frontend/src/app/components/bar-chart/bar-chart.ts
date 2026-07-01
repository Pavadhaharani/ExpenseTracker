import { Component, Input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

export interface BarChartItem {
  label: string;
  value: number;
}

@Component({
  selector: 'app-bar-chart',
  imports: [CurrencyPipe],
  templateUrl: './bar-chart.html',
  styleUrl: './bar-chart.scss'
})
export class BarChart {
  @Input({ required: true }) items: BarChartItem[] = [];

  maxValue(): number {
    return Math.max(...this.items.map((item) => Number(item.value) || 0), 1);
  }

  height(value: number): string {
    return `${Math.max(4, (Number(value) / this.maxValue()) * 100)}%`;
  }
}
