import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface RegisterRequest {
  email: string;
  password: string;
  role: string;
}

export interface OtpVerificationRequest {
  email: string;
  otp: string;
}

export interface ResendOtpRequest {
  email: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  otp: string;
  newPassword: string;
}



export interface UserProfile {
  email: string;
  name: string;
  phoneNumber: string;
  role: string;
  verified: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = '/api';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
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

  // User registration
  registerUser(request: RegisterRequest): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/users/register`, request, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // OTP verification
  verifyOtp(request: OtpVerificationRequest): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/users/verify-otp`, request, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Resend OTP
  resendOtp(request: ResendOtpRequest): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/users/resend-otp`, request, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Forgot password
  forgotPassword(request: ForgotPasswordRequest): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/auth/forgot-password`, request, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Reset password
  resetPassword(request: ResetPasswordRequest): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.post(`${this.baseUrl}/auth/reset-password`, request, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get user profile
  getUserProfile(email: string): Observable<UserProfile> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.get<UserProfile>(`${this.baseUrl}/profile/${email}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update user profile
  updateUserProfile(email: string, updates: Partial<UserProfile>): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain, application/json'
    });

    return this.http.put(`${this.baseUrl}/profile/update/${email}`, updates, { headers, responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }
} 