import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FoodItem {
  id?: number;        // Optional for new items
  name: string;
  price: number;
  available: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FoodService {
  // Change base URL to your backend server address
  private baseUrl = '/food';

  constructor(private http: HttpClient) {}

  // GET /food/all — Admin/Controller now exposes this for full list
  getAllItems(): Observable<FoodItem[]> {
    return this.http.get<FoodItem[]>(`${this.baseUrl}/all`);
  }

  // Backward-compatible alias for older callers
  getMenu(): Observable<FoodItem[]> {
    return this.getAllItems();
  }

  // GET /food/available — Requires ADMIN or USER role
  getAvailableItems(): Observable<FoodItem[]> {
    return this.http.get<FoodItem[]>(`${this.baseUrl}/available`);
  }

  // POST /food/add — Requires ADMIN role
  addFoodItem(foodItem: FoodItem): Observable<FoodItem> {
    return this.http.post<FoodItem>(`${this.baseUrl}/add`, foodItem);
  }

  // DELETE /food/{id} — Requires ADMIN role
  deleteFoodItem(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
