import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RiskService, RiskCountByType } from '../../services/risk';
import { Chart, ChartConfiguration, ChartType, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {
  riskCounts: RiskCountByType = {};
  chart: Chart | null = null;

  Object = Object;

  constructor(
    private riskService: RiskService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadRiskCounts();
  }

  loadRiskCounts(): void {
    this.riskService.getRiskCountByType().subscribe({
      next: (data) => {
        this.riskCounts = data;
        this.createPieChart();
      },
      error: (error) => {
        console.error('Error loading risk counts:', error);
      }
    });
  }

  createPieChart(): void {
    const ctx = document.getElementById('riskChart') as HTMLCanvasElement;
    if (!ctx) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const labels = Object.keys(this.riskCounts);
    const data = Object.values(this.riskCounts);
    const colors = [
      '#FF6384',
      '#36A2EB', 
      '#FFCE56',
      '#4BC0C0',
      '#9966FF'
    ];

    const config: ChartConfiguration = {
      type: 'pie' as ChartType,
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: colors.slice(0, labels.length),
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Non-Closed Risks by Type'
          },
          legend: {
            position: 'bottom'
          }
        },
        onClick: (event, elements) => {
          if (elements.length > 0) {
            const index = elements[0].index;
            const riskType = labels[index];
            this.navigateToRiskDetails(riskType);
          }
        }
      }
    };

    this.chart = new Chart(ctx, config);
  }

  navigateToRiskDetails(riskType?: string): void {
    if (riskType) {
      this.router.navigate(['/risk-details'], { queryParams: { type: riskType } });
    } else {
      this.router.navigate(['/risk-details']);
    }
  }

  getTotalRisks(): number {
    return Object.values(this.riskCounts).reduce((sum, count) => sum + count, 0);
  }
}
