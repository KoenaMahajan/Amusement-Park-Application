import { Injectable } from '@angular/core';
import { Observable, of, throwError, delay } from 'rxjs';
import { RegisterRequest, OtpVerificationRequest, ResendOtpRequest } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class MockUserService {
  
  private registeredUsers: string[] = [];
  private otpStore: { [email: string]: string } = {};

  registerUser(request: RegisterRequest): Observable<string> {
    console.log('Mock: Registering user:', request);
    
    // Store the user and generate OTP
    this.registeredUsers.push(request.email);
    const otp = this.generateOtp();
    this.otpStore[request.email] = otp;
    console.log(`Mock: OTP for ${request.email} is: ${otp}`);
    
    // Simulate network delay
    return of('Registered successfully. OTP sent to console (valid for 5 min).').pipe(
      delay(1000)
    );
  }

  verifyOtp(request: OtpVerificationRequest): Observable<string> {
    console.log('Mock: Verifying OTP:', request);
    
    const storedOtp = this.otpStore[request.email];
    
    if (!storedOtp) {
      return throwError(() => new Error('OTP expired or not found.'));
    }
    
    if (storedOtp !== request.otp) {
      return throwError(() => new Error('Invalid OTP.'));
    }
    
    // Clear OTP after successful verification
    delete this.otpStore[request.email];
    
    return of('OTP verified. Account activated.').pipe(
      delay(1000)
    );
  }

  resendOtp(request: ResendOtpRequest): Observable<string> {
    console.log('Mock: Resending OTP for:', request.email);
    
    if (!this.registeredUsers.includes(request.email)) {
      return throwError(() => new Error('User not found.'));
    }
    
    // Generate new OTP
    const otp = this.generateOtp();
    this.otpStore[request.email] = otp;
    console.log(`Mock: New OTP for ${request.email} is: ${otp}`);
    
    return of('OTP resent to your email (console for now).').pipe(
      delay(1000)
    );
  }

  private generateOtp(): string {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  // Method to get stored OTP (for testing only)
  getStoredOtp(email: string): string | undefined {
    return this.otpStore[email];
  }
} 