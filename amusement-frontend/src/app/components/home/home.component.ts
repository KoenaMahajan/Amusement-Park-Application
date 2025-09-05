import { Component,ViewChild,ElementRef,AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  navigateToRegister() {
    this.router.navigate(['/register']);
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  // Role-based navigation
  navigateToDashboard() {
    if (this.authService.isLoggedIn()) {
      if (this.authService.isAdmin()) {
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.router.navigate(['/user/dashboard']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  //video related 
  @ViewChild('bgVideo') bgVideo!: ElementRef<HTMLVideoElement>;

ngAfterViewInit() {
  const video = this.bgVideo.nativeElement;
  video.muted = true;
  video.play().catch(err => console.log('Autoplay blocked:', err));
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

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  getCurrentUser() {
    return this.authService.getCurrentUser();
  }
}