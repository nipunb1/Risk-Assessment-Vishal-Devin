import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RiskService, Risk } from '../../services/risk';

@Component({
  selector: 'app-risk-details',
  imports: [CommonModule],
  templateUrl: './risk-details.html',
  styleUrl: './risk-details.css'
})
export class RiskDetailsComponent implements OnInit {
  risks: Risk[] = [];
  filteredRisks: Risk[] = [];
  loading = false;
  selectedRiskType: string | null = null;

  riskTypes = [
    'MARKET_PRACTICE',
    'CONFLICT_OF_INTEREST', 
    'PRICING',
    'REGULATORY',
    'GOVERNANCE'
  ];

  riskStatuses = [
    'OPEN',
    'IN_PROGRESS',
    'CLOSED'
  ];

  constructor(
    private riskService: RiskService,
    private route: ActivatedRoute,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.selectedRiskType = params['type'] || null;
      this.loadRisks();
    });
  }

  loadRisks(): void {
    this.loading = true;
    
    if (this.selectedRiskType) {
      this.riskService.getRisksByType(this.selectedRiskType).subscribe({
        next: (data) => {
          this.risks = data;
          this.filteredRisks = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading risks by type:', error);
          this.loading = false;
        }
      });
    } else {
      this.riskService.getAllRisks().subscribe({
        next: (data) => {
          this.risks = data;
          this.filteredRisks = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading all risks:', error);
          this.loading = false;
        }
      });
    }
  }

  filterByStatus(status: string): void {
    if (status === 'ALL') {
      this.filteredRisks = this.risks;
    } else {
      this.filteredRisks = this.risks.filter(risk => risk.riskStatus === status);
    }
  }

  editRisk(risk: Risk): void {
    this.router.navigate(['/create-risk'], { queryParams: { id: risk.riskId } });
  }

  deleteRisk(risk: Risk): void {
    if (confirm(`Are you sure you want to delete risk: ${risk.riskDesc}?`)) {
      this.riskService.deleteRisk(risk.riskId!).subscribe({
        next: () => {
          this.loadRisks();
        },
        error: (error) => {
          console.error('Error deleting risk:', error);
          alert('Error deleting risk. Please try again.');
        }
      });
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  getRiskTypeDisplay(riskType: string): string {
    const typeMap: { [key: string]: string } = {
      'MARKET_PRACTICE': 'Market Practice',
      'CONFLICT_OF_INTEREST': 'Conflict of Interest',
      'PRICING': 'Pricing',
      'REGULATORY': 'Regulatory',
      'GOVERNANCE': 'Governance'
    };
    return typeMap[riskType] || riskType;
  }

  getRiskProbabilityDisplay(probability: string): string {
    const probMap: { [key: string]: string } = {
      'LOW': 'Low',
      'MEDIUM': 'Medium',
      'HIGH': 'High'
    };
    return probMap[probability] || probability;
  }

  getRiskStatusDisplay(status: string): string {
    const statusMap: { [key: string]: string } = {
      'OPEN': 'Open',
      'IN_PROGRESS': 'In Progress',
      'CLOSED': 'Closed'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const classMap: { [key: string]: string } = {
      'OPEN': 'status-open',
      'IN_PROGRESS': 'status-in-progress',
      'CLOSED': 'status-closed'
    };
    return classMap[status] || '';
  }
}
