import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AdminService, User, UserUpdateRequest } from '../../../services/admin.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  isLoading = false;
  message = '';
  messageType = '';
  editingUser: User | null = null;
  editForm: FormGroup;

  constructor(
    private adminService: AdminService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.editForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      role: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: (error) => {
        this.message = error.message || 'Failed to load users';
        this.messageType = 'error';
        this.isLoading = false;
      }
    });
  }

  editUser(user: User) {
    this.editingUser = user;
    this.editForm.patchValue({
      name: user.name,
      phoneNumber: user.phoneNumber,
      role: user.role
    });
  }

  cancelEdit() {
    this.editingUser = null;
    this.editForm.reset();
  }

  saveUser() {
    if (this.editForm.valid && this.editingUser) {
      const updates: UserUpdateRequest = {};
      
      if (this.editForm.get('name')?.value !== this.editingUser.name) {
        updates.name = this.editForm.get('name')?.value;
      }
      if (this.editForm.get('phoneNumber')?.value !== this.editingUser.phoneNumber) {
        updates.phoneNumber = this.editForm.get('phoneNumber')?.value;
      }
      if (this.editForm.get('role')?.value !== this.editingUser.role) {
        updates.role = this.editForm.get('role')?.value;
      }

      if (Object.keys(updates).length > 0) {
        this.adminService.updateUser(this.editingUser.id, updates).subscribe({
          next: (response) => {
            this.message = 'User updated successfully';
            this.messageType = 'success';
            this.loadUsers();
            this.cancelEdit();
          },
          error: (error) => {
            this.message = error.message || 'Failed to update user';
            this.messageType = 'error';
          }
        });
      } else {
        this.cancelEdit();
      }
    }
  }

  deleteUser(user: User) {
    if (confirm(`Are you sure you want to delete user ${user.name}?`)) {
      this.adminService.deleteUser(user.id).subscribe({
        next: (response) => {
          this.message = 'User deleted successfully';
          this.messageType = 'success';
          this.loadUsers();
        },
        error: (error) => {
          this.message = error.message || 'Failed to delete user';
          this.messageType = 'error';
        }
      });
    }
  }

  navigateToAdminDashboard() {
    this.router.navigate(['/admin/dashboard']);
  }

  getErrorMessage(field: string): string {
    const control = this.editForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return `${field} is required.`;
      if (control.errors['minlength']) return `${field} must be at least 2 characters.`;
      if (control.errors['pattern']) return 'Phone number must be 10 digits.';
    }
    return '';
  }
}
