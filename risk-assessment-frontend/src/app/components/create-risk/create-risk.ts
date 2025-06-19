import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RiskService, Risk } from '../../services/risk';

@Component({
  selector: 'app-create-risk',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-risk.html',
  styleUrl: './create-risk.css'
})
export class CreateRiskComponent implements OnInit {
  risk: Risk = {
    riskDate: new Date().toISOString().split('T')[0],
    riskType: 'MARKET_PRACTICE',
    riskProbability: 'MEDIUM',
    riskDesc: '',
    riskStatus: 'OPEN',
    riskRemarks: ''
  };

  isEditMode = false;
  loading = false;
  riskId: number | null = null;

  riskTypes = [
    { value: 'MARKET_PRACTICE', label: 'Market Practice' },
    { value: 'CONFLICT_OF_INTEREST', label: 'Conflict of Interest' },
    { value: 'PRICING', label: 'Pricing' },
    { value: 'REGULATORY', label: 'Regulatory' },
    { value: 'GOVERNANCE', label: 'Governance' }
  ];

  riskProbabilities = [
    { value: 'LOW', label: 'Low' },
    { value: 'MEDIUM', label: 'Medium' },
    { value: 'HIGH', label: 'High' }
  ];

  riskStatuses = [
    { value: 'OPEN', label: 'Open' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'CLOSED', label: 'Closed' }
  ];

  maxDate: string;

  constructor(
    private riskService: RiskService,
    private route: ActivatedRoute,
    public router: Router
  ) {
    this.maxDate = new Date().toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.riskId = +params['id'];
        this.isEditMode = true;
        this.loadRisk();
      }
    });
  }

  loadRisk(): void {
    if (!this.riskId) return;

    this.loading = true;
    this.riskService.getRiskById(this.riskId).subscribe({
      next: (data) => {
        this.risk = {
          ...data,
          riskDate: new Date(data.riskDate).toISOString().split('T')[0]
        };
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading risk:', error);
        alert('Error loading risk for editing. Redirecting to create mode.');
        this.router.navigate(['/create-risk']);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    const riskData = { ...this.risk };

    if (this.isEditMode && this.riskId) {
      this.riskService.updateRisk(this.riskId, riskData).subscribe({
        next: (response) => {
          alert('Risk updated successfully!');
          this.router.navigate(['/risk-details']);
          this.loading = false;
        },
        error: (error) => {
          console.error('Error updating risk:', error);
          alert('Error updating risk. Please try again.');
          this.loading = false;
        }
      });
    } else {
      this.riskService.createRisk(riskData).subscribe({
        next: (response) => {
          alert('Risk created successfully!');
          this.router.navigate(['/risk-details']);
          this.loading = false;
        },
        error: (error) => {
          console.error('Error creating risk:', error);
          alert('Error creating risk. Please try again.');
          this.loading = false;
        }
      });
    }
  }

  validateForm(): boolean {
    if (!this.risk.riskDesc.trim()) {
      alert('Risk description is required.');
      return false;
    }

    if (!this.risk.riskDate) {
      alert('Risk date is required.');
      return false;
    }

    const selectedDate = new Date(this.risk.riskDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (selectedDate > today) {
      alert('Risk date cannot be in the future.');
      return false;
    }

    return true;
  }

  resetForm(): void {
    this.risk = {
      riskDate: new Date().toISOString().split('T')[0],
      riskType: 'MARKET_PRACTICE',
      riskProbability: 'MEDIUM',
      riskDesc: '',
      riskStatus: 'OPEN',
      riskRemarks: ''
    };
  }

  cancel(): void {
    if (confirm('Are you sure you want to cancel? Any unsaved changes will be lost.')) {
      this.router.navigate(['/risk-details']);
    }
  }
}
