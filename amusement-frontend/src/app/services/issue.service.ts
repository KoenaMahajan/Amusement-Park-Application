import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export type IssueStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';

export interface IssueRequest {
  subject: string;
  description: string;
}

export interface IssueResponse {
  id: number;
  subject: string;
  description: string;
  status: IssueStatus;
  reportedBy: string;
  createdAt: string;
  resolvedAt?: string | null;
}

@Injectable({ providedIn: 'root' })
export class IssueService {
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
  reportIssue(request: IssueRequest): Observable<IssueResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'Accept': 'application/json' });
    return this.http
      .post<IssueResponse>(`${this.baseUrl}/issues/report`, request, { headers })
      .pipe(catchError(this.handleError));
  }

  getMyIssues(): Observable<IssueResponse[]> {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    return this.http
      .get<IssueResponse[]>(`${this.baseUrl}/issues/user/all`, { headers })
      .pipe(catchError(this.handleError));
  }

  // ADMIN
  getAllIssues(): Observable<IssueResponse[]> {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    return this.http
      .get<IssueResponse[]>(`${this.baseUrl}/issues/all`, { headers })
      .pipe(catchError(this.handleError));
  }

  getIssuesByStatus(status: IssueStatus): Observable<IssueResponse[]> {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    return this.http
      .get<IssueResponse[]>(`${this.baseUrl}/issues/status?status=${encodeURIComponent(status)}`, { headers })
      .pipe(catchError(this.handleError));
  }

  updateIssueStatus(id: number, status: IssueStatus): Observable<IssueResponse> {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    return this.http
      .patch<IssueResponse>(`${this.baseUrl}/issues/status/${id}?status=${encodeURIComponent(status)}`, {}, { headers })
      .pipe(catchError(this.handleError));
  }
}


