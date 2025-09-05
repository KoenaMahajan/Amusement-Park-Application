import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ItemType } from './order.service';

export interface CartItem {
  itemId: number;
  itemType: ItemType;
  itemName: string;
  unitPrice: number;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private itemsSubject = new BehaviorSubject<CartItem[]>([]);
  public items$ = this.itemsSubject.asObservable();

  getItems(): CartItem[] {
    return this.itemsSubject.value;
  }

  addItem(item: Omit<CartItem, 'quantity'>, quantity: number = 1): void {
    const current = this.itemsSubject.value.slice();
    const idx = current.findIndex(
      i => i.itemId === item.itemId && i.itemType === item.itemType
    );
    if (idx >= 0) {
      current[idx] = { ...current[idx], quantity: current[idx].quantity + quantity };
    } else {
      current.push({ ...item, quantity });
    }
    this.itemsSubject.next(current);
  }

  updateQuantity(itemId: number, itemType: ItemType, quantity: number): void {
    const current = this.itemsSubject.value.slice();
    const idx = current.findIndex(i => i.itemId === itemId && i.itemType === itemType);
    if (idx >= 0) {
      current[idx] = { ...current[idx], quantity };
      if (current[idx].quantity <= 0) {
        current.splice(idx, 1);
      }
      this.itemsSubject.next(current);
    }
  }

  removeItem(itemId: number, itemType: ItemType): void {
    const filtered = this.itemsSubject.value.filter(i => !(i.itemId === itemId && i.itemType === itemType));
    this.itemsSubject.next(filtered);
  }

  clear(): void {
    this.itemsSubject.next([]);
  }
}



