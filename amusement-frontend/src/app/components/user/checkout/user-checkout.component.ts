import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CartService, CartItem } from '../../../services/cart.service';
import { OrderService, OrderRequest } from '../../../services/order.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-user-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './user-checkout.component.html',
  styleUrls: ['./user-checkout.component.css']
})
export class UserCheckoutComponent implements OnInit {
  cartItems: CartItem[] = [];
  checkoutForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private cart: CartService,
    private fb: FormBuilder,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {
    this.checkoutForm = this.fb.group({
      pickupLocation: ['', Validators.required],
      couponCode: ['']
    });
  }

  ngOnInit(): void {
    this.cartItems = this.cart.getItems();
    if (this.cartItems.length === 0) {
      this.router.navigate(['/user/store']);
      return;
    }
    this.loadPickupLocations();
  }

  get totalAmount(): number {
    return this.cartItems.reduce((total, item) => total + (item.quantity * item.unitPrice), 0);
  }

  get totalItems(): number {
    return this.cartItems.reduce((total, item) => total + item.quantity, 0);
  }

  loadPickupLocations() {
    this.orderService.getPickupLocations().subscribe({
      next: (locs) => {
        if (locs.length > 0) {
          this.checkoutForm.patchValue({ pickupLocation: locs[0] });
        }
      },
      error: (err) => this.errorMessage = err.message || 'Failed to load pickup locations'
    });
  }

  placeOrder() {
    if (this.checkoutForm.invalid || this.cartItems.length === 0) return;

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.errorMessage = 'You must be logged in to place an order.';
      return;
    }

    const payload: OrderRequest = {
      userId: Number(currentUser.id),
      pickupLocation: this.checkoutForm.value.pickupLocation,
      itemList: this.cartItems.map(item => ({
        itemId: item.itemId,
        itemType: item.itemType,
        quantity: item.quantity,
        itemName: item.itemName,
        unitPrice: item.unitPrice
      }))
    };

    const coupon = this.checkoutForm.value.couponCode || undefined;
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.orderService.placeOrder(payload, coupon).subscribe({
      next: (order) => {
        this.successMessage = `Order #${order.id} placed successfully!`;
        this.cart.clear();
        this.isLoading = false;
        // Don't auto-redirect, let user choose where to go
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to place order';
        this.isLoading = false;
      }
    });
  }

  goBackToStore() {
    this.router.navigate(['/user/store']);
  }
}
