import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UserServiceProvider } from '../../services/user.service.provider';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent {
  registrationForm: FormGroup;
  isLoading = false;
  message = '';
  messageType = '';
  showOtp = false;
  generatedOtp = '';

  constructor(
    private fb: FormBuilder,
    private userServiceProvider: UserServiceProvider,
    private router: Router
  ) {
    this.registrationForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])/)
      ]],
      confirmPassword: ['', [Validators.required]],
      role: ['USER', Validators.required] // ✅ RESTORED: Role selection back to original
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }

  onSubmit() {
    if (this.registrationForm.valid) {
      this.isLoading = true;
      this.message = '';
      this.showOtp = false;
      
      const formValue = this.registrationForm.value;
      const request = {
        email: formValue.email,
        password: formValue.password,
        role: formValue.role // ✅ RESTORED: Use selected role from form
      };

      console.log('Sending registration request:', request);

      this.userServiceProvider.service.registerUser(request).subscribe({
        next: (response) => {
          console.log('Registration response:', response);
          this.message = 'Registration successful! Please check your email for OTP.';
          this.messageType = 'success';
          this.isLoading = false;
          
          // Navigate to OTP verification page after 3 seconds
          setTimeout(() => {
            this.router.navigate(['/verify-otp'], { 
              queryParams: { email: formValue.email } 
            });
          }, 3000);
        },
        error: (error) => {
          console.error('Registration error:', error);
          this.message = error.message || 'Registration failed. Please try again.';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    }
  }

  getErrorMessage(field: string): string {
    const control = this.registrationForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return `${field} is required.`;
      if (control.errors['email']) return 'Please enter a valid email.';
      if (control.errors['minlength']) {
        return `${field} must be at least 8 characters.`;
      }
      if (control.errors['pattern']) {
        return `${field} must contain uppercase, lowercase, number, and special character.`;
      }
      if (control.errors['passwordMismatch']) {
        return 'Passwords do not match.';
      }
    }
    return '';
  }
} 