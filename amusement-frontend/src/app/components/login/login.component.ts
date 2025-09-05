import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  forgotPasswordForm: FormGroup;
  resetPasswordForm: FormGroup;
  isLoading = false;
  message = '';
  messageType = '';
  showForgotPassword = false;
  showResetPassword = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    this.resetPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      otp: ['', [Validators.required]],
      newPassword: ['', [
        Validators.required, 
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/)
      ]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.message = '';

      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe({
        next: (user) => {
          this.message = `Welcome back, ${user.name}!`;
          this.messageType = 'success';
          this.isLoading = false;

          // Fetch complete user profile
          this.authService.fetchUserProfile(email).subscribe({
            next: (completeProfile) => {
              // Navigate based on role
              setTimeout(() => {
                if (completeProfile.role === 'ADMIN') {
                  this.router.navigate(['/admin/dashboard']);
                } else {
                  this.router.navigate(['/user/dashboard']);
                }
              }, 2000);
            },
            error: (error) => {
              console.error('Failed to fetch user profile:', error);
              // Still navigate even if profile fetch fails
              setTimeout(() => {
                if (user.role === 'ADMIN') {
                  this.router.navigate(['/admin/dashboard']);
                } else {
                  this.router.navigate(['/user/dashboard']);
                }
              }, 2000);
            }
          });
        },
        error: (error) => {
          this.message = 'Login failed. Please check your credentials.';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    }
  }

  showForgotPasswordForm(event: Event) {
    event.preventDefault();
    this.showForgotPassword = true;
    this.showResetPassword = false;
    this.message = '';
    this.messageType = '';
  }

  onForgotPasswordSubmit() {
    if (this.forgotPasswordForm.valid) {
      this.isLoading = true;
      this.message = '';
      
      const { email } = this.forgotPasswordForm.value;
      
      // Call the auth service to send reset code
      this.authService.forgotPassword(email).subscribe({
        next: () => {
          this.message = 'Reset code sent to your email. Please check your inbox.';
          this.messageType = 'success';
          this.isLoading = false;
          // Show reset password form
          this.showResetPassword = true;
          this.showForgotPassword = false;
        },
        error: (error: any) => {
          this.message = 'Failed to send reset code. Please try again.';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    }
  }

  onResetPasswordSubmit() {
    if (this.resetPasswordForm.valid) {
      this.isLoading = true;
      this.message = '';
      
      const { email, otp, newPassword } = this.resetPasswordForm.value;
      
      // Call the auth service to reset password
      this.authService.resetPassword(email, otp, newPassword).subscribe({
        next: () => {
          this.message = 'Password reset successful! You can now login with your new password.';
          this.messageType = 'success';
          this.isLoading = false;
          // Reset forms and go back to login
          setTimeout(() => {
            this.backToLogin();
          }, 2000);
        },
        error: (error: any) => {
          this.message = 'Password reset failed. Please check your reset code and try again.';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    }
  }

  backToLogin() {
    this.showForgotPassword = false;
    this.showResetPassword = false;
    this.message = '';
    this.messageType = '';
    this.forgotPasswordForm.reset();
    this.resetPasswordForm.reset();
  }

  // Demo functions for testing
  loginAsAdmin() {
    this.authService.switchToAdmin();
    this.router.navigate(['/admin/dashboard']);
  }

  loginAsUser() {
    this.authService.switchToUser();
    this.router.navigate(['/user/dashboard']);
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }

  navigateToHome() {
    this.router.navigate(['/']);
  }

  getErrorMessage(field: string): string {
    const control = this.loginForm.get(field);
    if (control?.hasError('required')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is required.`;
    }
    if (control?.hasError('email')) {
      return 'Please enter a valid email address.';
    }
    if (control?.hasError('minlength')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} must be at least 6 characters.`;
    }
    return '';
  }
} 