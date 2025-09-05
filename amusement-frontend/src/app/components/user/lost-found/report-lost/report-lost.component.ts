import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LostFoundService, LostAndFoundRequest } from '../../../../services/lost-found.service';

@Component({
  selector: 'app-report-lost',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-lost.component.html',
  styleUrls: ['./report-lost.component.css']
})
export class ReportLostComponent {
  form: FormGroup;
  isSubmitting = false;
  message: string | null = null;
  messageType: 'success' | 'error' | null = null;

  constructor(private fb: FormBuilder, private lfService: LostFoundService) {
    this.form = this.fb.group({
      itemName: ['', [Validators.required, Validators.minLength(2)]],
      location: ['', [Validators.required, Validators.minLength(2)]],
      description: ['', [Validators.required, Validators.minLength(5)]],
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.isSubmitting = true;
    const payload: LostAndFoundRequest = this.form.value as LostAndFoundRequest;
    this.lfService.report(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.messageType = 'success';
        this.message = 'Report submitted successfully!';
        this.form.reset();
      },
      error: (err: Error) => {
        this.isSubmitting = false;
        this.messageType = 'error';
        this.message = err.message || 'Failed to submit report.';
      }
    });
  }
}


