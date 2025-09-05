import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface User {
  id: number;
  email: string;
  name: string;
  phoneNumber: string;
  role: string;
  verified: boolean;
}

export interface UserUpdateRequest {
  role?: string;
  phoneNumber?: string;
  name?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl = '/api';

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

  // Get all users
  getAllUsers(): Observable<User[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.get<User[]>(`${this.baseUrl}/admin/users`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update user role, phone number, and name
  updateUser(id: number, updates: UserUpdateRequest): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    // Build query parameters
    let params = '';
    if (updates.role) params += `role=${updates.role}&`;
    if (updates.phoneNumber) params += `phoneNumber=${updates.phoneNumber}&`;
    if (updates.name) params += `name=${updates.name}&`;
    
    // Remove trailing '&' if exists
    if (params.endsWith('&')) {
      params = params.slice(0, -1);
    }

    const url = params ? `${this.baseUrl}/admin/update/${id}?${params}` : `${this.baseUrl}/admin/update/${id}`;

    return this.http.put(url, {}, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Delete user
  deleteUser(id: number): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.delete<string>(`${this.baseUrl}/admin/delete/${id}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }
}
