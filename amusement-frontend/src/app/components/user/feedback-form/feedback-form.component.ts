import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FeedbackRequest, FeedbackService } from '../../../services/feedback.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-feedback-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './feedback-form.component.html',
  styleUrls: ['./feedback-form.component.css']
})
export class FeedbackFormComponent {
  feedbackForm: FormGroup;
  isSubmitting = false;
  message: string | null = null;
  messageType: 'success' | 'error' | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private feedbackService: FeedbackService,
    private router: Router
  ) {
    this.feedbackForm = this.formBuilder.group({
      rideRating: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
      cleanlinessRating: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
      staffBehaviorRating: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
      foodQualityRating: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
      comments: ['']
    });
  }

  submit(): void {
    if (this.feedbackForm.invalid) {
      this.feedbackForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.message = null;
    const payload: FeedbackRequest = this.feedbackForm.value as FeedbackRequest;

    this.feedbackService.submitFeedback(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.messageType = 'success';
        this.message = 'Thank you! Your feedback has been submitted.';
        this.feedbackForm.reset({
          rideRating: 3,
          cleanlinessRating: 3,
          staffBehaviorRating: 3,
          foodQualityRating: 3,
          comments: ''
        });
      },
      error: (err: Error) => {
        this.isSubmitting = false;
        this.messageType = 'error';
        this.message = err.message || 'Failed to submit feedback.';
      }
    });
  }
}


