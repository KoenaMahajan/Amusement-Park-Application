import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IssueResponse, IssueService, IssueStatus } from '../../../services/issue.service';

@Component({
  selector: 'app-issue-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './issue-management.component.html',
  styleUrls: ['./issue-management.component.css']
})
export class IssueManagementComponent implements OnInit {
  issues: IssueResponse[] = [];
  isLoading = false;
  message: string | null = null;
  statusFilter: IssueStatus | '' = '';
  statuses: IssueStatus[] = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

  constructor(private issueService: IssueService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;
    this.message = null;
    this.issueService.getAllIssues().subscribe({
      next: (data) => {
        this.issues = data;
        this.isLoading = false;
      },
      error: (err: Error) => {
        this.message = err.message;
        this.isLoading = false;
      }
    });
  }

  applyStatusFilter(): void {
    if (!this.statusFilter) {
      this.loadAll();
      return;
    }
    this.isLoading = true;
    this.message = null;
    this.issueService.getIssuesByStatus(this.statusFilter).subscribe({
      next: (data) => {
        this.issues = data;
        this.isLoading = false;
      },
      error: (err: Error) => {
        this.message = err.message;
        this.isLoading = false;
      }
    });
  }

  updateStatus(issueId: number, newStatus: IssueStatus): void {
    this.isLoading = true;
    this.message = null;
    this.issueService.updateIssueStatus(issueId, newStatus).subscribe({
      next: () => {
        this.applyStatusFilter();
      },
      error: (err: Error) => {
        this.message = err.message;
        this.isLoading = false;
      }
    });
  }
}


