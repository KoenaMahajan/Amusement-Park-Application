import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FoodItem, FoodService } from '../../../services/food.service';

@Component({
  selector: 'app-food-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './food-menu.component.html',
  styleUrls: ['./food-menu.component.css']
})
export class FoodMenuComponent implements OnInit {
  items: FoodItem[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(private foodService: FoodService) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.isLoading = true;
    this.errorMessage = '';
    // Backend now exposes /food/all, derive available items locally
    this.foodService.getMenu().subscribe({
      next: (items) => {
        // Show all items since backend may not include an 'available' flag
        this.items = items || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load food';
        this.isLoading = false;
      }
    });
  }
}


