import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MembershipService, MembershipPlan, UserMembership } from '../../../services/membership.service';
import { AdminService, User } from '../../../services/admin.service';
import { FoodService, FoodItem } from '../../../services/food.service'; // Food Mgmt


@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  membershipPlans: MembershipPlan[] = [];
  userMemberships: UserMembership[] = [];
  allUsers: User[] = [];
  isLoading = false;
  membershipStats = {
    totalPlans: 0,
    totalMembers: 0,
    totalUsers: 0,
    activeMembers: 0,
    expiredMembers: 0,
    totalRevenue: 0
  };

  // Food management statistics (removed card, keeping stats optional)
  availableFoodItems: number = 0;
  totalFoodItems: number = 0;

  constructor(
    private router: Router,
    private membershipService: MembershipService,
    private adminService: AdminService,
    private foodService: FoodService
  ) {}

  ngOnInit() {
    this.loadMembershipData();
    this.loadUsersData();
    this.loadFoodData();
  }

  loadUsersData() {
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.allUsers = users;
        this.membershipStats.totalUsers = users.length;
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.membershipStats.totalUsers = 0;
      }
    });
  }

  loadMembershipData() {
    this.isLoading = true;
    
    // Load membership plans
    this.membershipService.getAllMembershipPlans().subscribe({
      next: (plans) => {
        this.membershipPlans = plans;
        this.membershipStats.totalPlans = plans.length;
        this.calculateMembershipStats();
      },
      error: (error) => {
        console.error('Failed to load membership plans:', error);
      }
    });

    // Load user memberships
    this.membershipService.getAllUserMemberships().subscribe({
      next: (memberships) => {
        this.userMemberships = memberships;
        this.calculateMembershipStats();
      },
      error: (error) => {
        console.error('Failed to load user memberships:', error);
      }
    });
  }

  calculateMembershipStats() {
    if (this.membershipPlans.length > 0 && this.userMemberships.length > 0) {
      this.membershipStats.totalMembers = this.userMemberships.length;
      this.membershipStats.activeMembers = this.userMemberships.filter(m => m.status === 'ACTIVE').length;
      this.membershipStats.expiredMembers = this.userMemberships.filter(m => m.status === 'EXPIRED').length;
      
      // Calculate total revenue from active memberships
      this.membershipStats.totalRevenue = this.userMemberships
        .filter(m => m.status === 'ACTIVE')
        .reduce((total, membership) => total + membership.plan.price, 0);
    }
  }

  getUsersCountForPlan(planId: number): number {
    return this.userMemberships.filter(membership => membership.plan.id === planId).length;
  }

  getTopMembershipPlan(): MembershipPlan | null {
    if (this.membershipPlans.length === 0) return null;
    
    return this.membershipPlans.reduce((top, current) => {
      const currentCount = this.getUsersCountForPlan(current.id!);
      const topCount = this.getUsersCountForPlan(top.id!);
      return currentCount > topCount ? current : top;
    });
  }

   loadFoodData() {
    // Load all items from /food/all and derive available count locally
    this.foodService.getMenu().subscribe({
      next: (menu: FoodItem[]) => {
        this.totalFoodItems = menu.length;
        this.availableFoodItems = menu.filter(item => item.available).length;
      },
      error: (err) => {
        console.error('Failed to load menu:', err);
        this.totalFoodItems = 0;
        this.availableFoodItems = 0;
      }
    });
  }

  navigateToUserManagement() {
    this.router.navigate(['/admin/users']);
  }

  navigateToMembershipManagement() {
    this.router.navigate(['/admin/memberships']);
  }

  navigateToFeedbackManagement() {
    this.router.navigate(['/admin/feedback']);
  }

  navigateToIssueManagement() {
    this.router.navigate(['/admin/issues']);
  }

  navigateToChatbotManagement() {
    this.router.navigate(['/admin/chatbot']);
  }

  navigateToFeedbackAnalytics() {
    this.router.navigate(['/admin/feedback-analytics']);
  }

  navigateToLostFoundManagement() {
    this.router.navigate(['/admin/lost-found']);
  }

  navigateToOrders() {
    this.router.navigate(['/admin/orders']);
  }

  navigateToHome() {
    this.router.navigate(['/']);
  }
  // Removed Food Management card and navigation

  navigateToMerchManagement() {
    this.router.navigate(['/admin/merch']);
  }

  navigateToFoodManagement() {
    this.router.navigate(['/admin/food-management']);
  }

  navigateToRideManagement() {
    this.router.navigate(['/admin/rides']);
  }

  navigateToFavoritesManagement() {
    this.router.navigate(['/admin/favorites']);
  }

  navigateToMaintenanceManagement() {
    this.router.navigate(['/admin/maintenance']);
  }

  navigateToTicketManagement() {
    this.router.navigate(['/admin/tickets']);
  }
} 