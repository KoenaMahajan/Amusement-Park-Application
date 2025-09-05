import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LostAndFoundResponse, LostFoundService, LostFoundStatus } from '../../../services/lost-found.service';

@Component({
  selector: 'app-lost-found-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lost-found-management.component.html',
  styleUrls: ['./lost-found-management.component.css']
})
export class LostFoundManagementComponent implements OnInit {
  entries: LostAndFoundResponse[] = [];
  isLoading = false;
  message: string | null = null;
  dateFilter = '';
  statuses: LostFoundStatus[] = ['LOST', 'FOUND', 'RETURNED'];

  constructor(private lfService: LostFoundService) {}

  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.isLoading = true;
    this.message = null;
    this.lfService.getAll().subscribe({
      next: (data) => { this.entries = data; this.isLoading = false; },
      error: (err: Error) => { this.message = err.message; this.isLoading = false; }
    });
  }

  applyDateFilter(): void {
    if (!this.dateFilter) { this.loadAll(); return; }
    this.isLoading = true;
    this.lfService.getByDate(this.dateFilter).subscribe({
      next: (data) => { this.entries = data; this.isLoading = false; },
      error: (err: Error) => { this.message = err.message; this.isLoading = false; }
    });
  }

  updateStatus(id: number, status: LostFoundStatus): void {
    this.isLoading = true;
    this.lfService.updateStatus(id, status).subscribe({
      next: () => this.applyDateFilter(),
      error: (err: Error) => { this.message = err.message; this.isLoading = false; }
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this entry?')) return;
    this.lfService.deleteEntry(id).subscribe({
      next: () => this.applyDateFilter(),
      error: (err: Error) => this.message = err.message
    });
  }
}


