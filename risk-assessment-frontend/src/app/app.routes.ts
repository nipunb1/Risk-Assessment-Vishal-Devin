import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard';
import { RiskDetailsComponent } from './components/risk-details/risk-details';
import { CreateRiskComponent } from './components/create-risk/create-risk';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'risk-details', component: RiskDetailsComponent },
  { path: 'create-risk', component: CreateRiskComponent },
  { path: '**', redirectTo: '/dashboard' }
];
