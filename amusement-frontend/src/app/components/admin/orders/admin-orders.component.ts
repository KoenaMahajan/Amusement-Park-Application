import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService, OrderResponse } from '../../../services/order.service';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = false;
  errorMessage = '';
  selectedStatus = '';
  searchOrderId: number | null = null;
  isSearchMode = false;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll() {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (orders) => { this.orders = orders; this.isLoading = false; },
      error: (err) => { this.errorMessage = err.message || 'Failed to load orders'; this.isLoading = false; }
    });
  }

  filterByStatus() {
    if (!this.selectedStatus) { this.loadAll(); return; }
    this.isLoading = true;
    this.orderService.getOrdersByStatus(this.selectedStatus).subscribe({
      next: (orders) => { this.orders = orders; this.isLoading = false; },
      error: (err) => { this.errorMessage = err.message || 'Failed to load orders'; this.isLoading = false; }
    });
  }

  markPickedUp(orderId: number) {
    this.orderService.markAsPickedUp(orderId).subscribe({
      next: () => this.loadAll(),
      error: (err) => this.errorMessage = err.message || 'Failed to update order'
    });
  }

  searchOrderById() {
    if (!this.searchOrderId) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    this.isSearchMode = true;
    
    this.orderService.getOrderById(this.searchOrderId).subscribe({
      next: (order) => {
        this.orders = [order]; // Show only the searched order
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Order not found';
        this.orders = [];
        this.isLoading = false;
      }
    });
  }

  clearSearch() {
    this.searchOrderId = null;
    this.isSearchMode = false;
    this.errorMessage = '';
    this.loadAll(); // Reload all orders
  }
}


