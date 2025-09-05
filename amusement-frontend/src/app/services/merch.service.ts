import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface MerchItem {
  id?: number;
  name: string;
  price: number;
}

@Injectable({
  providedIn: 'root'
})
export class MerchService {
  private baseUrl = '/merch';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (error.status === 404) {
        errorMessage = 'API endpoint not found. Please check the backend URL.';
      } else if (error.status === 409) {
        errorMessage = typeof error.error === 'string' ? error.error : 'Duplicate merchandise item name.';
      } else if (error.status === 403) {
        errorMessage = 'You do not have permission to perform this action.';
      } else {
        errorMessage = (typeof error.error === 'string' && error.error) || `Server error: ${error.status}`;
      }
    }

    return throwError(() => new Error(errorMessage));
  }

  getStoreItems(): Observable<MerchItem[]> {
    return this.http.get<MerchItem[]>(`${this.baseUrl}/store`).pipe(catchError(this.handleError));
  }

  addMerchItem(item: MerchItem): Observable<MerchItem> {
    return this.http.post<MerchItem>(`${this.baseUrl}/add`, item).pipe(catchError(this.handleError));
  }

  deleteMerchItem(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`, { responseType: 'text' }).pipe(catchError(this.handleError));
  }
}


