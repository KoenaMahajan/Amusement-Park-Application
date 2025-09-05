import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MembershipService, MembershipPlan, UserMembership } from '../../../services/membership.service';
import { AuthService, User } from '../../../services/auth.service';
import { UserService, UserProfile } from '../../../services/user.service';
import { OrderService, OrderResponse } from '../../../services/order.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  membershipPlans: MembershipPlan[] = [];
  userMemberships: UserMembership[] = [];
  isLoading = false;
  message = '';
  messageType = '';
  showMemberships = false;
  
  // Profile management
  currentUser: User | null = null;
  userProfile: UserProfile | null = null;
  isEditingProfile = false;
  profileForm: FormGroup;
  isProfileLoading = false;

  // Orders management
  userOrders: OrderResponse[] = [];
  isOrdersLoading = false;

  constructor(
    private membershipService: MembershipService,
    private router: Router,
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private orderService: OrderService
  ) {
    this.profileForm = this.fb.group({
      name: ['', [Validators.minLength(2)]],
      phoneNumber: ['', [Validators.pattern(/^[0-9]{10}$/)]]
    });
  }

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    console.log('UserDashboard initialized with user:', this.currentUser);
    this.loadMembershipPlans();
    this.loadUserMemberships();
    this.loadUserProfile();
    this.loadUserOrders();
    
    // Debug current state
    setTimeout(() => {
      this.debugMembershipState();
    }, 1000);
  }

  loadMembershipPlans() {
    this.isLoading = true;
    this.membershipService.getAllMembershipPlans().subscribe({
      next: (plans) => {
        this.membershipPlans = plans;
        this.isLoading = false;
      },
      error: (error) => {
        this.message = error.message || 'Failed to load membership plans';
        this.messageType = 'error';
        this.isLoading = false;
      }
    });
  }

  loadUserMemberships() {
    console.log('Loading user memberships...');
    this.membershipService.viewMyMemberships().subscribe({
      next: (memberships) => {
        console.log('Received memberships:', memberships);
        this.userMemberships = memberships || [];
        console.log('Processed memberships array:', this.userMemberships);
        console.log('Memberships length:', this.userMemberships.length);
      },
      error: (error) => {
        console.error('Failed to load user memberships:', error);
        this.message = error.message || 'Failed to load memberships';
        this.messageType = 'error';
        this.userMemberships = [];
      }
    });
  }

  loadUserProfile() {
    if (this.currentUser?.email) {
      this.isProfileLoading = true;
      this.userService.getUserProfile(this.currentUser.email).subscribe({
        next: (profile) => {
          this.userProfile = profile;
          this.profileForm.patchValue({
            name: profile.name || '',
            phoneNumber: profile.phoneNumber || ''
          });
          this.isProfileLoading = false;
        },
        error: (error) => {
          console.error('Failed to load user profile:', error);
          // If profile loading fails, create a default profile with current user data
          if (this.currentUser) {
            this.userProfile = {
              email: this.currentUser.email,
              name: this.currentUser.name || '',
              phoneNumber: this.currentUser.phoneNumber || '',
              role: this.currentUser.role,
              verified: this.currentUser.verified || false
            };
            this.profileForm.patchValue({
              name: this.userProfile.name,
              phoneNumber: this.userProfile.phoneNumber
            });
          }
          this.isProfileLoading = false;
        }
      });
    }
  }

  toggleProfileEditing() {
    this.isEditingProfile = !this.isEditingProfile;
    if (this.isEditingProfile) {
      this.profileForm.patchValue({
        name: this.userProfile?.name || '',
        phoneNumber: this.userProfile?.phoneNumber || ''
      });
    }
  }

  saveProfile() {
    if (this.profileForm.valid && this.currentUser?.email) {
      this.isProfileLoading = true;
      const updates = this.profileForm.value;
      
      this.userService.updateUserProfile(this.currentUser.email, updates).subscribe({
        next: (response) => {
          this.message = 'Profile updated successfully!';
          this.messageType = 'success';
          this.isProfileLoading = false;
          this.isEditingProfile = false;
          
          // Update local user data
          if (this.userProfile) {
            this.userProfile.name = updates.name;
            this.userProfile.phoneNumber = updates.phoneNumber;
          }
          
          // Update auth service current user
          if (this.currentUser) {
            this.currentUser.name = updates.name;
            this.currentUser.phoneNumber = updates.phoneNumber;
            this.authService.updateCurrentUser(this.currentUser);
          }
          
          // Clear message after 3 seconds
          setTimeout(() => {
            this.message = '';
            this.messageType = '';
          }, 3000);
        },
        error: (error) => {
          this.message = error.message || 'Failed to update profile';
          this.messageType = 'error';
          this.isProfileLoading = false;
        }
      });
    }
  }

  cancelProfileEdit() {
    this.isEditingProfile = false;
    this.profileForm.patchValue({
      name: this.userProfile?.name || '',
      phoneNumber: this.userProfile?.phoneNumber || ''
    });
  }

  subscribeToPlan(planId: number) {
    console.log('Subscribing to plan:', planId);
    if (confirm(`Are you sure you want to subscribe to this plan?`)) {
      this.membershipService.subscribeToPlan(planId).subscribe({
        next: (response) => {
          console.log('Subscription successful:', response);
          this.message = 'Successfully subscribed to plan!';
          this.messageType = 'success';
          
          // Clear message after 3 seconds
          setTimeout(() => {
            this.message = '';
            this.messageType = '';
          }, 3000);
          
          // Reload memberships to show the new subscription
          console.log('Reloading memberships after subscription...');
          this.loadUserMemberships();
          
          // Auto-show memberships if they were hidden
          if (!this.showMemberships) {
            this.showMemberships = true;
            console.log('Auto-showing memberships after subscription');
          }
        },
        error: (error) => {
          console.error('Subscription failed:', error);
          this.message = error.message || 'Failed to subscribe to plan';
          this.messageType = 'error';
          
          // Clear error message after 5 seconds
          setTimeout(() => {
            this.message = '';
            this.messageType = '';
          }, 5000);
        }
      });
    }
  }

  cancelMembership() {
    if (confirm('Are you sure you want to cancel your membership?')) {
      this.membershipService.cancelMembership().subscribe({
        next: (response) => {
          this.message = 'Membership cancelled successfully';
          this.messageType = 'success';
          this.loadUserMemberships();
        },
        error: (error) => {
          this.message = error.message || 'Failed to cancel membership';
          this.messageType = 'error';
        }
      });
    }
  }

  hasActiveMembership(): boolean {
    return this.userMemberships.some(membership => membership.status === 'ACTIVE');
  }

  getActiveMembership(): UserMembership | undefined {
    return this.userMemberships.find(membership => membership.status === 'ACTIVE');
  }

  loadUserOrders() {
    // 🔒 SECURITY FIX: Use secure endpoint that doesn't expose user ID
    this.isOrdersLoading = true;
    
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.userOrders = orders || [];
        this.isOrdersLoading = false;
      },
      error: (error) => {
        console.error('Failed to load user orders:', error);
        this.userOrders = [];
        this.isOrdersLoading = false;
      }
    });
  }

  getCurrentOrders(): OrderResponse[] {
    return this.userOrders.filter(order => 
      order.status !== 'PICKED_UP' && order.status !== 'CANCELLED'
    );
  }

  getPreviousOrders(): OrderResponse[] {
    return this.userOrders.filter(order => 
      order.status === 'PICKED_UP' || order.status === 'CANCELLED'
    );
  }

  navigateToHome() {
    this.router.navigate(['/']);
  }

  toggleMemberships() {
    console.log('=== TOGGLE MEMBERSHIPS CALLED ===');
    console.log('Before toggle - showMemberships:', this.showMemberships);
    console.log('Before toggle - userMemberships:', this.userMemberships);
    console.log('Before toggle - userMemberships length:', this.userMemberships.length);
    
    this.showMemberships = !this.showMemberships;
    
    console.log('After toggle - showMemberships:', this.showMemberships);
    console.log('After toggle - userMemberships:', this.userMemberships);
    
    // If showing memberships and we don't have them loaded yet, load them
    if (this.showMemberships) {
      if (this.userMemberships.length === 0) {
        console.log('No memberships loaded, calling loadUserMemberships...');
        this.loadUserMemberships();
      } else {
        console.log('Memberships already loaded, count:', this.userMemberships.length);
      }
    }
    
    // Debug the current state after toggle
    setTimeout(() => {
      this.debugMembershipState();
    }, 100);
  }

  navigateToFeedback() {
    this.router.navigate(['/user/feedback']);
  }

  navigateToReportIssue() {
    this.router.navigate(['/user/report-issue']);
  }

  navigateToOrders() {
    this.router.navigate(['/user/orders']);
  }

  debugMembershipState() {
    console.log('=== MEMBERSHIP DEBUG STATE ===');
    console.log('showMemberships:', this.showMemberships);
    console.log('userMemberships array:', this.userMemberships);
    console.log('userMemberships length:', this.userMemberships.length);
    console.log('userMemberships type:', typeof this.userMemberships);
    console.log('userMemberships is array:', Array.isArray(this.userMemberships));
    if (this.userMemberships.length > 0) {
      console.log('First membership:', this.userMemberships[0]);
    }
    console.log('=============================');
  }
}
