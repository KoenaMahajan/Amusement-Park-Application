import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LostAndFoundResponse, LostFoundService } from '../../../../services/lost-found.service';

@Component({
  selector: 'app-found-items',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './found-items.component.html',
  styleUrls: ['./found-items.component.css']
})
export class FoundItemsComponent implements OnInit {
  items: LostAndFoundResponse[] = [];
  isLoading = false;
  message: string | null = null;

  constructor(private lfService: LostFoundService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.isLoading = true;
    this.lfService.getFound().subscribe({
      next: (data) => { this.items = data; this.isLoading = false; },
      error: (err: Error) => { this.message = err.message; this.isLoading = false; }
    });
  }
}


