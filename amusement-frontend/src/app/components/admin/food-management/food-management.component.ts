import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { FoodService, FoodItem } from '../../../services/food.service';

@Component({
  selector: 'app-food-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './food-management.component.html',
  styleUrls: ['./food-management.component.css']
})
export class FoodManagementComponent implements OnInit {
  items: FoodItem[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  form!: FormGroup;

  constructor(private foodService: FoodService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      price: [0, [Validators.required, Validators.min(1)]]
    });
    this.load();
  }

  load() {
    this.isLoading = true;
    this.errorMessage = '';
    this.foodService.getMenu().subscribe({
      next: (list) => { this.items = list; this.isLoading = false; },
      error: (err) => { this.errorMessage = err.message || 'Failed to load menu'; this.isLoading = false; }
    });
  }

  add() {
    if (this.form.invalid) return;
    const payload: FoodItem = { name: this.form.value.name!, price: Number(this.form.value.price), available: true } as any;
    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.foodService.addFoodItem(payload).subscribe({
      next: (created) => {
        this.successMessage = `Added '${created.name}'`;
        this.form.reset({ name: '', price: 0 });
        this.load();
      },
      error: (err) => { this.errorMessage = err.message || 'Failed to add item'; this.isLoading = false; }
    });
  }

  deleteItem(id?: number) {
    if (!id) return;
    if (!confirm('Are you sure you want to delete this food item?')) return;
    
    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';
    
    this.foodService.deleteFoodItem(id).subscribe({
      next: () => {
        this.successMessage = 'Food item deleted successfully';
        this.load(); // Reload the list
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to delete item';
        this.isLoading = false;
      }
    });
  }

  getPriceErrorMessage(): string {
    const priceControl = this.form.get('price');
    if (priceControl?.hasError('min')) {
      return 'Price should not be less than 0';
    }
    if (priceControl?.hasError('required')) {
      return 'Price is required';
    }
    return '';
  }
}


