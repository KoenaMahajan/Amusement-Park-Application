import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define the enum types to match backend
export type AlertType = 'SCHEDULED_MAINTENANCE' | 'EMERGENCY_CLOSURE' | 'WEATHER_CLOSURE' | 'INSPECTION';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface MaintenanceAlertDto {
  id: number;
  rideId: number;
  rideName: string;
  alertType: AlertType;
  title: string;
  description?: string;
  startTime: string;
  endTime?: string;
  isActive: boolean;
  isCurrentlyActive: boolean;
  priority: Priority;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MaintenanceAlertCreateDto {
  rideId: number;
  alertType: AlertType;
  title: string;
  description?: string;
  startTime: string; // ISO string
  endTime?: string; // ISO string
  priority: Priority;
  createdBy?: string;
}

@Injectable({ providedIn: 'root' })
export class MaintenanceAlertService {
  private baseUrl = '/api/maintenance';

  constructor(private http: HttpClient) {}

  getAllActiveAlerts(): Observable<MaintenanceAlertDto[]> {
    return this.http.get<MaintenanceAlertDto[]>(`${this.baseUrl}/alerts`);
  }

  getAlertsByRideId(rideId: number): Observable<MaintenanceAlertDto[]> {
    return this.http.get<MaintenanceAlertDto[]>(`${this.baseUrl}/alerts/ride/${rideId}`);
  }

  getAlertById(id: number): Observable<MaintenanceAlertDto> {
    return this.http.get<MaintenanceAlertDto>(`${this.baseUrl}/alerts/${id}`);
  }

  createAlert(payload: MaintenanceAlertCreateDto): Observable<MaintenanceAlertDto> {
    console.log('Service: Creating alert with payload:', payload);
    console.log('Service: Request URL:', `${this.baseUrl}/alerts`);
    
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
    
    return this.http.post<MaintenanceAlertDto>(`${this.baseUrl}/alerts`, payload, { headers });
  }

  updateAlert(id: number, payload: MaintenanceAlertCreateDto): Observable<MaintenanceAlertDto> {
    console.log('Service: Updating alert with ID:', id, 'payload:', payload);
    console.log('Service: Request URL:', `${this.baseUrl}/alerts/${id}`);
    
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
    
    return this.http.put<MaintenanceAlertDto>(`${this.baseUrl}/alerts/${id}`, payload, { headers });
  }

  deleteAlert(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.baseUrl}/alerts/${id}`);
  }

  updateAlertStatus(id: number, isActive: boolean): Observable<MaintenanceAlertDto> {
    return this.http.patch<MaintenanceAlertDto>(`${this.baseUrl}/alerts/${id}/status`, { isActive });
  }

  getUpcomingMaintenance(): Observable<MaintenanceAlertDto[]> {
    return this.http.get<MaintenanceAlertDto[]>(`${this.baseUrl}/upcoming`);
  }

  getMaintenanceHistory(rideId: number): Observable<MaintenanceAlertDto[]> {
    return this.http.get<MaintenanceAlertDto[]>(`${this.baseUrl}/history/${rideId}`);
  }

  getCurrentlyActiveAlerts(): Observable<MaintenanceAlertDto[]> {
    return this.http.get<MaintenanceAlertDto[]>(`${this.baseUrl}/active`);
  }
}


