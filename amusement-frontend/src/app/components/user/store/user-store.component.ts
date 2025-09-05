import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FoodItem, FoodService } from '../../../services/food.service';
import { MerchItem, MerchService } from '../../../services/merch.service';
import { CartService } from '../../../services/cart.service';
import { ItemType } from '../../../services/order.service';

@Component({
  selector: 'app-user-store',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user-store.component.html',
  styleUrls: ['./user-store.component.css']
})
export class UserStoreComponent implements OnInit {
  food: FoodItem[] = [];
  merch: MerchItem[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  cartCount = 0;
  cartTotal = 0;

  constructor(
    private foodService: FoodService,
    private merchService: MerchService,
    private cart: CartService
  ) {}

  ngOnInit(): void {
    this.load();
    // Track cart summary
    this.cart.items$.subscribe(items => {
      this.cartCount = items.reduce((acc, it) => acc + it.quantity, 0);
      this.cartTotal = items.reduce((acc, it) => acc + it.quantity * it.unitPrice, 0);
    });
  }

  load() {
    this.isLoading = true;
    this.errorMessage = '';
    // Load food
    this.foodService.getMenu().subscribe({
      next: (items) => {
        this.food = items || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load food';
        this.isLoading = false;
      }
    });

    // Load merch
    this.merchService.getStoreItems().subscribe({
      next: (items) => { this.merch = items || []; },
      error: (err) => { this.errorMessage = err.message || 'Failed to load merchandise'; }
    });
  }

  addFoodToCart(item: FoodItem) {
    if (!item.id) return;
    this.cart.addItem({
      itemId: item.id,
      itemType: 'FOOD' as ItemType,
      itemName: item.name,
      unitPrice: item.price
    });
    this.successMessage = `Added '${item.name}' to cart`;
    setTimeout(() => this.successMessage = '', 1500);
  }

  addMerchToCart(item: MerchItem) {
    if (!item.id) return;
    this.cart.addItem({
      itemId: item.id,
      itemType: 'MERCH' as ItemType,
      itemName: item.name,
      unitPrice: item.price
    });
    this.successMessage = `Added '${item.name}' to cart`;
    setTimeout(() => this.successMessage = '', 1500);
  }

  clearCart() {
    this.cart.clear();
  }
}


