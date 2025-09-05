import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface FeedbackAnalytics {
  averageRideRating: number;
  averageCleanlinessRating: number;
  averageStaffBehaviorRating: number;
  averageFoodQualityRating: number;
  totalFeedbackCount: number;
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private baseUrl = 'http://localhost:8082';
  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.status === 0) errorMessage = 'Unable to connect to server.';
    else if (typeof error.error === 'string') errorMessage = error.error;
    else errorMessage = `Server error: ${error.status}`;
    return throwError(() => new Error(errorMessage));
  }

  getFeedbackAverages(): Observable<FeedbackAnalytics> {
    return this.http
      .get<FeedbackAnalytics>(`${this.baseUrl}/analytics/feedback/averages`)
      .pipe(catchError(this.handleError));
  }
}


