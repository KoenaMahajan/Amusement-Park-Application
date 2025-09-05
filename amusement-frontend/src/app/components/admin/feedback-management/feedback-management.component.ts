import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FeedbackResponse, FeedbackService } from '../../../services/feedback.service';

@Component({
  selector: 'app-feedback-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback-management.component.html',
  styleUrls: ['./feedback-management.component.css']
})
export class FeedbackManagementComponent implements OnInit {
  feedbackList: FeedbackResponse[] = [];
  isLoading = false;
  message: string | null = null;
  dateFilter: string = '';

  constructor(private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;
    this.message = null;
    this.feedbackService.getAllFeedback().subscribe({
      next: (data) => {
        this.feedbackList = data;
        this.isLoading = false;
      },
      error: (err: Error) => {
        this.message = err.message;
        this.isLoading = false;
      }
    });
  }

  applyDateFilter(): void {
    if (!this.dateFilter) {
      this.loadAll();
      return;
    }
    this.isLoading = true;
    this.message = null;
    this.feedbackService.getFeedbackByDate(this.dateFilter).subscribe({
      next: (data) => {
        this.feedbackList = data;
        this.isLoading = false;
      },
      error: (err: Error) => {
        this.message = err.message;
        this.isLoading = false;
      }
    });
  }
}


