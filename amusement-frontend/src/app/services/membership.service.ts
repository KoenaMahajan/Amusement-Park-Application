import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface MembershipPlan {
  id?: number;
  name: string;
  description: string;
  price: number;
  durationInDays: number;
}

export interface UserMembership {
  id?: number;
  user: any;
  plan: MembershipPlan;
  startDate: string;
  endDate?: string;
  status: string;
}

export interface MembershipPlanWithCount {
  count: number;
  memberships: UserMembership[];
}

@Injectable({
  providedIn: 'root'
})
export class MembershipService {
  private baseUrl = 'http://localhost:8082/api';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (error.status === 404) {
        errorMessage = 'API endpoint not found. Please check the backend URL.';
      } else if (error.status === 403) {
        errorMessage = 'Access forbidden. CORS issue detected.';
      } else {
        errorMessage = error.error || `Server error: ${error.status}`;
      }
    }

    console.error('HTTP Error:', error);
    return throwError(() => new Error(errorMessage));
  }

  // Get all membership plans
  getAllMembershipPlans(): Observable<MembershipPlan[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.get<MembershipPlan[]>(`${this.baseUrl}/membership-plans`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Add new membership plan (Admin only)
  addMembershipPlan(plan: MembershipPlan): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/admin/membership-plans`, plan, {
      headers,
      responseType: 'text'
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update membership plan (Admin only)
  updateMembershipPlan(id: number, updates: Partial<MembershipPlan>): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.patch(`${this.baseUrl}/admin/membership-plans/${id}`, updates, {
      headers,
      responseType: 'text'
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Delete membership plan (Admin only)
  deleteMembershipPlan(id: number): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.delete(`${this.baseUrl}/admin/membership-plans/${id}`, {
      headers,
      responseType: 'text'
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get users and count by membership plan (Admin only)
  getUsersAndCountByPlan(planId: number): Observable<MembershipPlanWithCount> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.get<MembershipPlanWithCount>(`${this.baseUrl}/admin/membership-plans/${planId}/users-with-count`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get all user memberships (Admin only)
  getAllUserMemberships(): Observable<UserMembership[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });



    return this.http.get<UserMembership[]>(`${this.baseUrl}/admin/user-memberships`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Subscribe to a membership plan
  subscribeToPlan(planId: number): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/user-memberships/subscribe/${planId}`, {}, {
      headers,
      responseType: 'text'
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // View my memberships
  viewMyMemberships(): Observable<UserMembership[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    console.log('Calling viewMyMemberships endpoint...');

    return this.http.get<UserMembership[]>(`${this.baseUrl}/user-memberships/my`, { headers })
      .pipe(
        catchError((error) => {
          console.error('Error in viewMyMemberships:', error);
          return this.handleError(error);
        })
      );
  }

  // Cancel membership
  cancelMembership(): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.put(`${this.baseUrl}/user-memberships/cancel`, {}, {
      headers,
      responseType: 'text'
    })
      .pipe(
        catchError(this.handleError)
      );
  }
}
