import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface FeedbackRequest {
  rideRating: number;
  cleanlinessRating: number;
  staffBehaviorRating: number;
  foodQualityRating: number;
  comments?: string;
}

export interface FeedbackResponse {
  id: number;
  userName: string;
  rideRating: number;
  cleanlinessRating: number;
  staffBehaviorRating: number;
  foodQualityRating: number;
  comments?: string;
  createdAt: string; // ISO string from backend
}

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private baseUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (error.status === 404) {
        errorMessage = 'Resource not found.';
      } else if (error.status === 403) {
        errorMessage = 'Access forbidden.';
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else {
        errorMessage = `Server error: ${error.status}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }

  getAllFeedback(): Observable<FeedbackResponse[]> {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    return this.http
      .get<FeedbackResponse[]>(`${this.baseUrl}/feedback/all`, { headers })
      .pipe(catchError(this.handleError));
  }

  submitFeedback(request: FeedbackRequest): Observable<FeedbackResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'Accept': 'application/json' });
    return this.http
      .post<FeedbackResponse>(`${this.baseUrl}/feedback/user`, request, { headers })
      .pipe(catchError(this.handleError));
  }

  getFeedbackByDate(date: string): Observable<FeedbackResponse[]> {
    const headers = new HttpHeaders({ 'Accept': 'application/json' });
    return this.http
      .get<FeedbackResponse[]>(`${this.baseUrl}/feedback/date?date=${encodeURIComponent(date)}`, { headers })
      .pipe(catchError(this.handleError));
  }
}


