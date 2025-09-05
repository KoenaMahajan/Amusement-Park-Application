import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService, FeedbackAnalytics } from '../../../services/analytics.service';

@Component({
  selector: 'app-feedback-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feedback-analytics.component.html',
  styleUrls: ['./feedback-analytics.component.css']
})
export class FeedbackAnalyticsComponent implements OnInit {
  data: FeedbackAnalytics | null = null;
  error: string | null = null;
  isLoading = false;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.analyticsService.getFeedbackAverages().subscribe({
      next: (d) => { this.data = d; this.isLoading = false; },
      error: (e: Error) => { this.error = e.message; this.isLoading = false; }
    });
  }
}


