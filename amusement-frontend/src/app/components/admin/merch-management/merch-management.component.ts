import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MerchItem, MerchService } from '../../../services/merch.service';

@Component({
  selector: 'app-merch-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './merch-management.component.html',
  styleUrls: ['./merch-management.component.css']
})
export class MerchManagementComponent implements OnInit {
  items: MerchItem[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  merchForm: FormGroup;

  constructor(private merchService: MerchService, private fb: FormBuilder) {
    this.merchForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      price: [0, [Validators.required, Validators.min(1)]]
    });
  }

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

  addItem() {
    if (this.merchForm.invalid) { return; }
    const payload: MerchItem = {
      name: this.merchForm.value.name,
      price: Number(this.merchForm.value.price)
    };
    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.merchService.addMerchItem(payload).subscribe({
      next: (item) => {
        this.successMessage = `Added '${item.name}'`;
        this.merchForm.reset({ name: '', price: 0 });
        this.loadItems();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to add item';
        this.isLoading = false;
      }
    });
  }

  deleteItem(id?: number) {
    if (!id) { return; }
    if (!confirm('Delete this merchandise item?')) { return; }
    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.merchService.deleteMerchItem(id).subscribe({
      next: () => {
        this.successMessage = 'Item deleted';
        this.loadItems();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to delete item';
        this.isLoading = false;
      }
    });
  }

  getPriceErrorMessage(): string {
    const priceControl = this.merchForm.get('price');
    if (priceControl?.hasError('min')) {
      return 'Price should not be less than Rs. 1';
    }
    if (priceControl?.hasError('required')) {
      return 'Price is required';
    }
    return '';
  }
}


