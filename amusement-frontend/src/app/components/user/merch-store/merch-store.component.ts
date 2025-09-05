import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MerchItem, MerchService } from '../../../services/merch.service';

@Component({
  selector: 'app-merch-store',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './merch-store.component.html',
  styleUrls: ['./merch-store.component.css']
})
export class MerchStoreComponent implements OnInit {
  items: MerchItem[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(private merchService: MerchService) {}

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems() {
    this.isLoading = true;
    this.errorMessage = '';
    this.merchService.getStoreItems().subscribe({
      next: (items) => {
        this.items = items;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load merchandise';
        this.isLoading = false;
      }
    });
  }
}


