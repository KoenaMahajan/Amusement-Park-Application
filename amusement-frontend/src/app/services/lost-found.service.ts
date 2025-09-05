import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export type LostFoundStatus = 'LOST' | 'FOUND' | 'RETURNED';

export interface LostAndFoundRequest {
  itemName: string;
  description: string;
  status?: LostFoundStatus;
  location: string;
}

export interface LostAndFoundResponse {
  id: number;
  itemName: string;
  description: string;
  status: LostFoundStatus;
  location: string;
  reportedBy: string;
  reportedAt: string;
}

@Injectable({ providedIn: 'root' })
export class LostFoundService {
  private baseUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else {
        errorMessage = `Server error: ${error.status}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }

  // USER
  report(request: LostAndFoundRequest): Observable<LostAndFoundResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http
      .post<LostAndFoundResponse>(`${this.baseUrl}/lost-and-found/report`, request, { headers })
      .pipe(catchError(this.handleError));
  }

  myReports(): Observable<LostAndFoundResponse[]> {
    return this.http
      .get<LostAndFoundResponse[]>(`${this.baseUrl}/lost-and-found/my-reports`)
      .pipe(catchError(this.handleError));
  }

  getFound(): Observable<LostAndFoundResponse[]> {
    return this.http
      .get<LostAndFoundResponse[]>(`${this.baseUrl}/lost-and-found/found`)
      .pipe(catchError(this.handleError));
  }

  deleteMyReport(id: number): Observable<string> {
    return this.http
      .delete(`${this.baseUrl}/lost-and-found/user/delete/${id}`, { responseType: 'text' })
      .pipe(catchError(this.handleError));
  }

  // ADMIN
  getAll(): Observable<LostAndFoundResponse[]> {
    return this.http
      .get<LostAndFoundResponse[]>(`${this.baseUrl}/lost-and-found/all`)
      .pipe(catchError(this.handleError));
  }

  deleteEntry(id: number): Observable<string> {
    return this.http
      .delete(`${this.baseUrl}/lost-and-found/admin/delete/${id}`, { responseType: 'text' })
      .pipe(catchError(this.handleError));
  }

  updateStatus(id: number, status: LostFoundStatus): Observable<LostAndFoundResponse> {
    return this.http
      .patch<LostAndFoundResponse>(`${this.baseUrl}/lost-and-found/status/${id}?status=${status}`, {})
      .pipe(catchError(this.handleError));
  }

  getByDate(date: string): Observable<LostAndFoundResponse[]> {
    return this.http
      .get<LostAndFoundResponse[]>(`${this.baseUrl}/lost-and-found/admin/by-date?date=${encodeURIComponent(date)}`)
      .pipe(catchError(this.handleError));
  }
}


