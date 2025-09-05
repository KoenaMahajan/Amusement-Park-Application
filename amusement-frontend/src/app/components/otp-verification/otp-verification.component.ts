import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { UserService, OtpVerificationRequest, ResendOtpRequest } from '../../services/user.service';

@Component({
  selector: 'app-otp-verification',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './otp-verification.component.html',
  styleUrls: ['./otp-verification.component.css']
})
export class OtpVerificationComponent implements OnInit {
  otpForm: FormGroup;
  isLoading = false;
  message = '';
  messageType = '';
  email = '';
  showResendButton = false;
  countdown = 0;

  constructor(
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]]
    });
  }

  ngOnInit() {
    // Get email from route params or query params
    this.route.params.subscribe(params => {
      this.email = params['email'] || '';
    });
    
    this.route.queryParams.subscribe(params => {
      if (!this.email) {
        this.email = params['email'] || '';
      }
    });

    if (!this.email) {
      this.router.navigate(['/registration']);
      return;
    }

    // Start countdown for resend button
    this.startCountdown();
  }

  startCountdown() {
    this.countdown = 300; // 5 minutes in seconds
    this.showResendButton = false;
    
    const timer = setInterval(() => {
      this.countdown--;
      if (this.countdown <= 0) {
        clearInterval(timer);
        this.showResendButton = true;
      }
    }, 1000);
  }

  onSubmit() {
    if (this.otpForm.valid) {
      this.isLoading = true;
      this.message = '';
      
      const request: OtpVerificationRequest = {
        email: this.email,
        otp: this.otpForm.get('otp')?.value
      };

      this.userService.verifyOtp(request).subscribe({
        next: (response) => {
          this.message = 'OTP verified successfully! Your account is now activated.';
          this.messageType = 'success';
          this.isLoading = false;
          
          // Redirect to login after 2 seconds
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (error) => {
          this.message = error.message || 'Failed to verify OTP';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    }
  }

  resendOtp() {
    this.isLoading = true;
    this.message = '';
    
    const request: ResendOtpRequest = {
      email: this.email
    };

    this.userService.resendOtp(request).subscribe({
      next: (response) => {
        this.message = 'OTP resent successfully! Please check your email.';
        this.messageType = 'success';
        this.isLoading = false;
        this.startCountdown();
      },
      error: (error) => {
        this.message = error.message || 'Failed to resend OTP';
        this.messageType = 'error';
        this.isLoading = false;
      }
    });
  }

  navigateToRegistration() {
    this.router.navigate(['/registration']);
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  formatCountdown(): string {
    const minutes = Math.floor(this.countdown / 60);
    const seconds = this.countdown % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  getErrorMessage(field: string): string {
    const control = this.otpForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return 'OTP is required.';
      if (control.errors['pattern']) return 'OTP must be 6 digits.';
    }
    return '';
  }
}
