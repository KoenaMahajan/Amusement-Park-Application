import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MembershipService, MembershipPlan, UserMembership } from '../../../services/membership.service';

@Component({
  selector: 'app-membership-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './membership-management.component.html',
  styleUrls: ['./membership-management.component.css']
})
export class MembershipManagementComponent implements OnInit {
  membershipPlans: MembershipPlan[] = [];
  userMemberships: UserMembership[] = [];
  isLoading = false;
  message = '';
  messageType = '';
  showAddForm = false;
  editingPlan: MembershipPlan | null = null;
  planForm: FormGroup;

  constructor(
    private membershipService: MembershipService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.planForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: ['', [Validators.required, Validators.min(0)]],
      durationInDays: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit() {
    this.loadMembershipPlans();
    this.loadUserMemberships();
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
    this.membershipService.getAllUserMemberships().subscribe({
      next: (memberships) => {
        this.userMemberships = memberships;
      },
      error: (error) => {
        console.error('Failed to load user memberships:', error);
      }
    });
  }

  showAddPlanForm() {
    this.showAddForm = true;
    this.editingPlan = null;
    this.planForm.reset();
  }

  editPlan(plan: MembershipPlan) {
    this.editingPlan = plan;
    this.showAddForm = false;
    this.planForm.patchValue({
      name: plan.name,
      description: plan.description,
      price: plan.price,
      durationInDays: plan.durationInDays
    });
  }

  cancelForm() {
    this.showAddForm = false;
    this.editingPlan = null;
    this.planForm.reset();
  }

  onSubmit() {
    if (this.planForm.valid) {
      const planData = this.planForm.value;
      
      if (this.editingPlan) {
        // Update existing plan
        this.membershipService.updateMembershipPlan(this.editingPlan.id!, planData).subscribe({
          next: (response) => {
            this.message = 'Membership plan updated successfully';
            this.messageType = 'success';
            this.loadMembershipPlans();
            this.cancelForm();
          },
          error: (error) => {
            this.message = error.message || 'Failed to update membership plan';
            this.messageType = 'error';
          }
        });
      } else {
        // Add new plan
        this.membershipService.addMembershipPlan(planData).subscribe({
          next: (response) => {
            this.message = 'Membership plan added successfully';
            this.messageType = 'success';
            this.loadMembershipPlans();
            this.cancelForm();
          },
          error: (error) => {
            this.message = error.message || 'Failed to add membership plan';
            this.messageType = 'error';
          }
        });
      }
    }
  }

  deletePlan(plan: MembershipPlan) {
    if (confirm(`Are you sure you want to delete the "${plan.name}" plan?`)) {
      this.membershipService.deleteMembershipPlan(plan.id!).subscribe({
        next: (response) => {
          this.message = 'Membership plan deleted successfully';
          this.messageType = 'success';
          this.loadMembershipPlans();
        },
        error: (error) => {
          this.message = error.message || 'Failed to delete membership plan';
          this.messageType = 'error';
        }
      });
    }
  }

  getUsersCountForPlan(planId: number): number {
    return this.userMemberships.filter(membership => membership.plan.id === planId).length;
  }

  getPlanStatistics(planId: number) {
    const planMemberships = this.userMemberships.filter(membership => membership.plan.id === planId);
    const activeMemberships = planMemberships.filter(m => m.status === 'ACTIVE');
    const expiredMemberships = planMemberships.filter(m => m.status === 'EXPIRED');
    
    return {
      total: planMemberships.length,
      active: activeMemberships.length,
      expired: expiredMemberships.length,
      revenue: activeMemberships.reduce((total, m) => total + m.plan.price, 0)
    };
  }

  getTotalRevenue(): number {
    return this.userMemberships
      .filter(m => m.status === 'ACTIVE')
      .reduce((total, membership) => total + membership.plan.price, 0);
  }

  getActiveMembersCount(): number {
    return this.userMemberships.filter(m => m.status === 'ACTIVE').length;
  }

  getExpiredMembersCount(): number {
    return this.userMemberships.filter(m => m.status === 'EXPIRED').length;
  }

  navigateToAdminDashboard() {
    this.router.navigate(['/admin/dashboard']);
  }

  getErrorMessage(field: string): string {
    const control = this.planForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return `${field} is required.`;
      if (control.errors['minlength']) {
        if (field === 'name') return 'Name must be at least 2 characters.';
        return 'Description must be at least 10 characters.';
      }
      if (control.errors['min']) {
        if (field === 'price') return 'Price must be greater than 0.';
        return 'Duration must be at least 1 day.';
      }
    }
    return '';
  }
}
