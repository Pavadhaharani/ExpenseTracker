import { Component, Input } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';

export interface DonutChartItem {
  label: string;
  value: number;
  percentage: number;
  color?: string;
}

@Component({
  selector: 'app-donut-chart',
  imports: [CurrencyPipe, DecimalPipe],
  templateUrl: './donut-chart.html',
  styleUrl: './donut-chart.scss'
})
export class DonutChart {
  @Input({ required: true }) items: DonutChartItem[] = [];
  @Input() total = 0;

  gradient(): string {
    if (!this.items.length) {
      return '#eef3fb';
    }

    let cursor = 0;
    const segments = this.items.map((item, index) => {
      const start = cursor;
      cursor += Number(item.percentage) || 0;
      const color = item.color || this.fallbackColor(index);
      return `${color} ${start}% ${cursor}%`;
    });

    return `conic-gradient(${segments.join(', ')})`;
  }

  fallbackColor(index: number): string {
    const colors = ['#2d83df', '#12a594', '#f5a524', '#db4c6c', '#7c5cff', '#60718a'];
    return colors[index % colors.length];
  }
}
