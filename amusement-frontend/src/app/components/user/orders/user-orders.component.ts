import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { OrderService, OrderRequest, OrderResponse, ItemType } from '../../../services/order.service';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-user-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './user-orders.component.html',
  styleUrls: ['./user-orders.component.css']
})
export class UserOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  orderForm: FormGroup;
  locations: string[] = [];

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private authService: AuthService,
    private cart: CartService
  ) {
    this.orderForm = this.fb.group({
      pickupLocation: ['', Validators.required],
      couponCode: [''],
      itemList: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadPickupLocations();
    // Prefill from cart if available
    const cartItems = this.cart.getItems();
    if (cartItems.length > 0) {
      cartItems.forEach(ci => {
        this.itemList.push(this.fb.group({
          itemId: [ci.itemId, [Validators.required, Validators.min(1)]],
          itemType: [ci.itemType as ItemType, Validators.required],
          quantity: [ci.quantity, [Validators.required, Validators.min(1)]]
        }));
      });
    } else {
      this.addItem();
    }
    this.loadMyOrders();
  }

  get itemList(): FormArray {
    return this.orderForm.get('itemList') as FormArray;
  }

  addItem() {
    this.itemList.push(this.fb.group({
      itemId: [null, [Validators.required, Validators.min(1)]],
      itemType: ['FOOD' as ItemType, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    }));
  }

  removeItem(index: number) {
    this.itemList.removeAt(index);
  }

  loadPickupLocations() {
    this.orderService.getPickupLocations().subscribe({
      next: (locs) => this.locations = locs,
      error: (err) => this.errorMessage = err.message || 'Failed to load pickup locations'
    });
  }

  loadMyOrders() {
    // 🔒 SECURITY FIX: Use secure endpoint that doesn't expose user ID
    this.isLoading = true;
    this.orderService.getMyOrders().subscribe({
      next: (orders) => { this.orders = orders; this.isLoading = false; },
      error: (err) => { this.errorMessage = err.message || 'Failed to load orders'; this.isLoading = false; }
    });
  }

  placeOrder() {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) { this.errorMessage = 'You must be logged in.'; return; }
    if (this.orderForm.invalid) { return; }

    const payload: OrderRequest = {
      userId: Number(currentUser.id),
      pickupLocation: this.orderForm.value.pickupLocation,
      itemList: this.orderForm.value.itemList
    };

    const coupon = this.orderForm.value.couponCode || undefined;
    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.orderService.placeOrder(payload, coupon).subscribe({
      next: (order) => {
        this.successMessage = `Order #${order.id} placed`;
        this.cart.clear();
        this.orderForm.reset({ pickupLocation: '', couponCode: '' });
        (this.orderForm.get('itemList') as FormArray).clear();
        this.addItem();
        this.loadMyOrders();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to place order';
        this.isLoading = false;
      }
    });
  }
}


