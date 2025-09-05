import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, tap } from 'rxjs/operators';

export interface User {
  id: string;
  email: string;
  role: 'USER' | 'ADMIN' | 'MEMBER';
  name?: string;
  phoneNumber?: string;
  verified?: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  message: string;
  error?: string;
  role?: string; // Add role to response
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = '/api'; // Fixed: Use proxy instead of direct backend URL
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is logged in from localStorage
    const savedUser = localStorage.getItem('currentUser');
    const savedToken = localStorage.getItem('authToken');
    
    if (savedUser && savedToken) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  login(email: string, password: string): Observable<User> {
    const loginRequest: LoginRequest = { email, password };
    
    return this.http.post<LoginResponse>(`${this.baseUrl}/auth/login`, loginRequest)
      .pipe(
        tap(response => {
          // Store the JWT token
          localStorage.setItem('authToken', response.token);
          
          // Create user object from response
          const user: User = {
            id: '1', // Default ID since backend doesn't return user object
            email: email,
            role: (response.role as 'USER' | 'ADMIN' | 'MEMBER') || 'USER', // Use role from response
            name: email.split('@')[0], // Default name from email
            verified: true // Assume verified after login
          };
          
          this.currentUserSubject.next(user);
          localStorage.setItem('currentUser', JSON.stringify(user));
        }),
        map(response => {
          // Return the user object with role from response
          const user: User = {
            id: '1',
            email: email,
            role: (response.role as 'USER' | 'ADMIN' | 'MEMBER') || 'USER',
            name: email.split('@')[0],
            verified: true
          };
          return user;
        })
      );
  }

  // Method to fetch and update complete user profile
  fetchUserProfile(email: string): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/profile/${email}`).pipe( // Fixed: Correct endpoint
      tap(profile => {
        // Update current user with complete profile information
        const updatedUser: User = {
          id: profile.id || '1',
          email: profile.email,
          role: profile.role as 'USER' | 'ADMIN' | 'MEMBER',
          name: profile.name,
          phoneNumber: profile.phoneNumber,
          verified: profile.verified
        };
        this.currentUserSubject.next(updatedUser);
        localStorage.setItem('currentUser', JSON.stringify(updatedUser));
      })
    );
  }

  logout(): void {
    this.currentUserSubject.next(null);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('authToken');
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  isAdmin(): boolean {
    return this.currentUserSubject.value?.role === 'ADMIN';
  }

  isUser(): boolean {
    return this.currentUserSubject.value?.role === 'USER';
  }

  isMember(): boolean {
    return this.currentUserSubject.value?.role === 'MEMBER';
  }

  getAuthToken(): string | null {
    return localStorage.getItem('authToken');
  }

  updateCurrentUser(updatedUser: User): void {
    this.currentUserSubject.next(updatedUser);
    localStorage.setItem('currentUser', JSON.stringify(updatedUser));
  }

  // Demo methods for testing role switching
  switchToAdmin(): void {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      const adminUser: User = {
        ...currentUser,
        role: 'ADMIN'
      };
      this.currentUserSubject.next(adminUser);
      localStorage.setItem('currentUser', JSON.stringify(adminUser));
    } else {
      // Create a demo admin user if no user is logged in
      const demoAdminUser: User = {
        id: 'demo-admin-1',
        email: 'admin@demo.com',
        role: 'ADMIN',
        name: 'Demo Admin',
        verified: true
      };
      this.currentUserSubject.next(demoAdminUser);
      localStorage.setItem('currentUser', JSON.stringify(demoAdminUser));
    }
  }

  switchToUser(): void {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      const regularUser: User = {
        ...currentUser,
        role: 'USER'
      };
      this.currentUserSubject.next(regularUser);
      localStorage.setItem('currentUser', JSON.stringify(regularUser));
    } else {
      // Create a demo user if no user is logged in
      const demoUser: User = {
        id: 'demo-user-1',
        email: 'user@demo.com',
        role: 'USER',
        name: 'Demo User',
        verified: true
      };
      this.currentUserSubject.next(demoUser);
      localStorage.setItem('currentUser', JSON.stringify(demoUser));
    }
  }

  // Forgot password method
  forgotPassword(email: string): Observable<string> {
    return this.http.post(`http://localhost:8082/auth/forgot-password`, { email }, { 
      responseType: 'text' 
    });
  }

  // Reset password method
  resetPassword(email: string, otp: string, newPassword: string): Observable<any> {
    return this.http.post(`http://localhost:8082/auth/reset-password`, {
      email,
      otp,
      newPassword
    }, { 
      responseType: 'text' 
    });
  }

} 