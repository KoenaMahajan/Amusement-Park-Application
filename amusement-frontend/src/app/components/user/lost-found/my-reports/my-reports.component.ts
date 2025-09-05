import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LostAndFoundResponse, LostFoundService } from '../../../../services/lost-found.service';

@Component({
  selector: 'app-my-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-reports.component.html',
  styleUrls: ['./my-reports.component.css']
})
export class MyReportsComponent implements OnInit {
  reports: LostAndFoundResponse[] = [];
  isLoading = false;
  message: string | null = null;

  constructor(private lfService: LostFoundService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.message = null;
    this.lfService.myReports().subscribe({
      next: (data) => { this.reports = data; this.isLoading = false; },
      error: (err: Error) => { this.message = err.message; this.isLoading = false; }
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this report?')) return;
    this.lfService.deleteMyReport(id).subscribe({
      next: () => this.load(),
      error: (err: Error) => this.message = err.message
    });
  }
}


