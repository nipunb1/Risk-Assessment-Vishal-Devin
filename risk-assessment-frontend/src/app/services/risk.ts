import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Risk {
  riskId?: number;
  riskDate: string;
  riskType: string;
  riskProbability: string;
  riskDesc: string;
  riskStatus: string;
  riskRemarks: string;
}

export interface RiskCountByType {
  [key: string]: number;
}

@Injectable({
  providedIn: 'root'
})
export class RiskService {
  private apiUrl = 'http://localhost:8080/api/risks';

  constructor(private http: HttpClient) { }

  getAllRisks(): Observable<Risk[]> {
    return this.http.get<Risk[]>(this.apiUrl);
  }

  getRiskById(id: number): Observable<Risk> {
    return this.http.get<Risk>(`${this.apiUrl}/${id}`);
  }

  createRisk(risk: Risk): Observable<Risk> {
    return this.http.post<Risk>(this.apiUrl, risk);
  }

  updateRisk(id: number, risk: Risk): Observable<Risk> {
    return this.http.put<Risk>(`${this.apiUrl}/${id}`, risk);
  }

  deleteRisk(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getNonClosedRisks(): Observable<Risk[]> {
    return this.http.get<Risk[]>(`${this.apiUrl}/non-closed`);
  }

  getRiskCountByType(): Observable<RiskCountByType> {
    return this.http.get<RiskCountByType>(`${this.apiUrl}/dashboard/counts`);
  }

  getRisksByType(riskType: string): Observable<Risk[]> {
    return this.http.get<Risk[]>(`${this.apiUrl}/type/${riskType}`);
  }
}
