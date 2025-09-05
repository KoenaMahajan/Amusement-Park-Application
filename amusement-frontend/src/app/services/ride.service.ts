import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ThrillLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'EXTREME' | string;

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first?: boolean;
  last?: boolean;
}

export interface MaintenanceAlertDto {
  id: number;
  rideId: number;
  rideName: string;
  alertType: string;
  title: string;
  description: string;
  startTime: string;
  endTime?: string;
  isActive: boolean;
  isCurrentlyActive: boolean;
  priority?: string;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface RidePhotoDto {
  id: number;
  photoUrl: string;
  caption?: string;
  isPrimary?: boolean;
  uploadedAt?: string;
}

export interface RideDto {
  id: number;
  name: string;
  description?: string;
  thrillLevel: ThrillLevel;
  minAge?: number;
  maxAge?: number;
  durationMinutes: number;
  heightRequirementCm?: number;
  photoUrl?: string;
  videoUrl?: string;
  isOperational: boolean;
  isAvailable?: boolean;
  locationDescription?: string;
  safetyInstructions?: string;
  createdAt?: string;
  updatedAt?: string;
  activeMaintenanceAlerts?: MaintenanceAlertDto[];
  favoritesCount?: number;
  photos?: RidePhotoDto[];
}

export interface RideCreateDto {
  name: string;
  description?: string;
  thrillLevel: ThrillLevel;
  minAge: number;
  maxAge?: number;
  durationMinutes: number;
  heightRequirementCm?: number;
  photoUrl?: string;
  videoUrl?: string;
  locationDescription?: string;
  safetyInstructions?: string;
}

export interface RideUpdateDto {
  name?: string;
  description?: string;
  thrillLevel?: ThrillLevel;
  minAge?: number;
  maxAge?: number;
  durationMinutes?: number;
  heightRequirementCm?: number;
  photoUrl?: string;
  videoUrl?: string;
  locationDescription?: string;
  safetyInstructions?: string;
}

@Injectable({ providedIn: 'root' })
export class RideService {
  private baseUrl = '/api/rides';

  constructor(private http: HttpClient) {}

  getRides(params: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: 'asc' | 'desc';
    thrillLevel?: ThrillLevel | null;
    minAge?: number | null;
    operational?: boolean | null;
  } = {}): Observable<Page<RideDto>> {
    let httpParams = new HttpParams();
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);
    if (params.direction) httpParams = httpParams.set('direction', params.direction);
    if (params.thrillLevel) httpParams = httpParams.set('thrillLevel', params.thrillLevel);
    if (params.minAge != null) httpParams = httpParams.set('minAge', params.minAge);
    if (params.operational != null) httpParams = httpParams.set('operational', params.operational);
    return this.http.get<Page<RideDto>>(this.baseUrl, { params: httpParams });
  }

  getRideById(id: number): Observable<RideDto> {
    return this.http.get<RideDto>(`${this.baseUrl}/${id}`);
  }

  createRide(payload: RideCreateDto): Observable<RideDto> {
    return this.http.post<RideDto>(this.baseUrl, payload);
  }

  updateRide(id: number, payload: RideUpdateDto): Observable<RideDto> {
    return this.http.put<RideDto>(`${this.baseUrl}/${id}`, payload);
  }

  deleteRide(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.baseUrl}/${id}`);
  }

  updateRideStatus(id: number, isOperational: boolean): Observable<RideDto> {
    return this.http.patch<RideDto>(`${this.baseUrl}/${id}/status`, { isOperational });
  }

  searchRides(params: {
    keyword?: string | null;
    thrillLevel?: ThrillLevel | null;
    suitableForAge?: number | null;
    operational?: boolean | null;
    page?: number;
    size?: number;
    sort?: string;
    direction?: 'asc' | 'desc';
  }): Observable<Page<RideDto>> {
    let httpParams = new HttpParams();
    if (params.keyword) httpParams = httpParams.set('keyword', params.keyword);
    if (params.thrillLevel) httpParams = httpParams.set('thrillLevel', params.thrillLevel);
    if (params.suitableForAge != null) httpParams = httpParams.set('suitableForAge', params.suitableForAge);
    if (params.operational != null) httpParams = httpParams.set('operational', params.operational);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);
    if (params.direction) httpParams = httpParams.set('direction', params.direction);
    return this.http.get<Page<RideDto>>(`${this.baseUrl}/search`, { params: httpParams });
  }

  getRidesSuitableForAge(age: number): Observable<RideDto[]> {
    return this.http.get<RideDto[]>(`${this.baseUrl}/suitable-for-age/${age}`);
    }

  getAvailableRides(): Observable<RideDto[]> {
    return this.http.get<RideDto[]>(`${this.baseUrl}/available`);
  }

  getRidesByThrillLevel(thrillLevel: ThrillLevel): Observable<RideDto[]> {
    return this.http.get<RideDto[]>(`${this.baseUrl}/thrill-level/${thrillLevel}`);
  }

  getMostPopularRides(limit = 10): Observable<RideDto[]> {
    const params = new HttpParams().set('limit', limit);
    return this.http.get<RideDto[]>(`${this.baseUrl}/popular`, { params });
  }
}


