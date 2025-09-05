import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { IssueRequest, IssueService } from '../../../services/issue.service';

@Component({
  selector: 'app-report-issue',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-issue.component.html',
  styleUrls: ['./report-issue.component.css']
})
export class ReportIssueComponent {
  issueForm: FormGroup;
  isSubmitting = false;
  message: string | null = null;
  messageType: 'success' | 'error' | null = null;

  constructor(private fb: FormBuilder, private issueService: IssueService) {
    this.issueForm = this.fb.group({
      subject: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  submit(): void {
    if (this.issueForm.invalid) {
      this.issueForm.markAllAsTouched();
      return;
    }
    this.isSubmitting = true;
    this.message = null;
    const payload: IssueRequest = this.issueForm.value as IssueRequest;

    this.issueService.reportIssue(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.messageType = 'success';
        this.message = 'Issue reported successfully!';
        this.issueForm.reset();
      },
      error: (err: Error) => {
        this.isSubmitting = false;
        this.messageType = 'error';
        this.message = err.message || 'Failed to report issue.';
      }
    });
  }
}


