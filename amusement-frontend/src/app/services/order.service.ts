import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export type ItemType = 'FOOD' | 'MERCH';

export interface OrderItemRequest {
  itemId: number;
  itemType: ItemType;
  quantity: number;
  itemName?: string;
  unitPrice?: number;
}

export interface OrderRequest {
  userId: number;
  pickupLocation: string;
  itemList: OrderItemRequest[];
}

export interface OrderResponse {
  id: number;
  userId: number;
  pickupLocation: string;
  status: string;
  totalAmount: number;
  orderTime: string;
  itemList: Array<{
    itemId: number;
    itemName: string;
    quantity: number;
    unitPrice: number;
    itemType: ItemType;
  }>;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private baseUrl = '/orders';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let msg = 'An error occurred';
    if (error.status === 0) msg = 'Unable to connect to server.';
    else if (error.status === 400) msg = typeof error.error === 'string' ? error.error : 'Bad request';
    else if (error.status === 404) msg = 'Order not found';
    else if (error.status === 403) msg = 'You do not have permission to perform this action.';
    else msg = (typeof error.error === 'string' && error.error) || `Server error: ${error.status}`;
    return throwError(() => new Error(msg));
  }

  placeOrder(order: OrderRequest, couponCode?: string): Observable<OrderResponse> {
    let params = new HttpParams();
    if (couponCode) params = params.set('couponCode', couponCode);
    return this.http.post<OrderResponse>(`${this.baseUrl}/place`, order, { params }).pipe(catchError(this.handleError));
  }

  updateStatus(orderId: number, status: string): Observable<OrderResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.put<OrderResponse>(`${this.baseUrl}/${orderId}/status`, {}, { params }).pipe(catchError(this.handleError));
  }

  getOrderById(orderId: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.baseUrl}/${orderId}`).pipe(catchError(this.handleError));
  }

  getOrdersByUser(userId: number, status?: string, sort: 'asc' | 'desc' = 'asc'): Observable<OrderResponse[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (sort) params = params.set('sort', sort);
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/user/${userId}`, { params }).pipe(catchError(this.handleError));
  }

  // 🔒 NEW SECURE METHOD: Get current user's orders without exposing user ID
  getMyOrders(status?: string, sort: 'asc' | 'desc' = 'asc'): Observable<OrderResponse[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (sort) params = params.set('sort', sort);
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/my-orders`, { params }).pipe(catchError(this.handleError));
  }

  deleteOrder(orderId: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${orderId}`, { responseType: 'text' }).pipe(catchError(this.handleError));
  }

  getAllOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}`).pipe(catchError(this.handleError));
  }

  getOrdersByStatus(status: string): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/status/${status}`).pipe(catchError(this.handleError));
  }

  markAsPickedUp(orderId: number): Observable<OrderResponse> {
    return this.http.put<OrderResponse>(`${this.baseUrl}/${orderId}/pickup`, {}).pipe(catchError(this.handleError));
  }

  getPickupLocations(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/pickup-locations`).pipe(catchError(this.handleError));
  }
}


