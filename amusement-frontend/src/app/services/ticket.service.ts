import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

// Updated interfaces to match backend DTOs
export interface TicketType {
  ticketTypeId?: number;
  name: string;
  description: string;
  price: number;
  validityDays: number;
  isVip: boolean;
}

export interface Ticket {
  ticketId?: number;
  userId: number;
  ticketType: TicketType;
  ticketCode: string;
  purchaseDate: string;
  validFrom: string;
  validTo: string;
  totalAmount: number;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED';
  paymentMode: 'UPI' | 'CARD' | 'WALLET' | 'CASH';
  invoiceId: string;
}

export interface TicketResponse {
  ticketId: number;
  ticketTypeId: number;
  ticketTypeName: string;
  ticketTypeDescription: string;
  ticketTypePrice: number;
  ticketTypeValidityDays: number;
  ticketTypeIsVip: boolean;
  ticketCode: string;
  purchaseDate: string;
  validFrom: string;
  validTo: string;
  totalAmount: number;
  isVip: boolean;
  paymentStatus: string;
  invoiceId: string;
}

export interface TicketCreateRequest {
  userId: number;
  ticketType: {
    ticketTypeId: number;
  };
  validFrom?: string;
  totalAmount: number;
  paymentStatus: string;
  paymentMode: string;
}

export interface TicketBookingRequest {
  ticketTypeId: number;
  visitDate: string;
  quantity: number;
  userId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private baseUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (error.status === 404) {
        errorMessage = 'API endpoint not found. Please check the backend URL.';
      } else if (error.status === 403) {
        errorMessage = 'Access forbidden. CORS issue detected.';
      } else {
        errorMessage = error.error || `Server error: ${error.status}`;
      }
    }
    
    console.error('HTTP Error:', error);
    return throwError(() => new Error(errorMessage));
  }

  // Get all ticket types
  getAllTicketTypes(): Observable<TicketType[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.get<TicketType[]>(`${this.baseUrl}/ticket-types`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Get ticket type by ID
  getTicketTypeById(id: number): Observable<TicketType> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.get<TicketType>(`${this.baseUrl}/ticket-types/${id}`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Add new ticket type (Admin only)
  addTicketType(ticketType: TicketType): Observable<TicketType> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.post<TicketType>(`${this.baseUrl}/ticket-types`, ticketType, { headers })
      .pipe(catchError(this.handleError));
  }

  // Update ticket type (Admin only)
  updateTicketType(id: number, updates: Partial<TicketType>): Observable<TicketType> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    const url = `${this.baseUrl}/ticket-types/${id}`;
    console.log('Updating ticket type at URL:', url);
    console.log('Update data:', updates);
    return this.http.put<TicketType>(url, updates, { headers })
      .pipe(catchError(this.handleError));
  }

  // Delete ticket type (Admin only)
  deleteTicketType(id: number): Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.delete<void>(`${this.baseUrl}/ticket-types/${id}`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Get all tickets
  getAllTickets(): Observable<Ticket[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.get<Ticket[]>(`${this.baseUrl}/tickets`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Get ticket by ID
  getTicketById(id: number): Observable<Ticket> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.get<Ticket>(`${this.baseUrl}/tickets/${id}`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Create a new ticket
  createTicket(request: TicketCreateRequest): Observable<TicketResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.post<TicketResponse>(`${this.baseUrl}/tickets/create`, request, { headers })
      .pipe(catchError(this.handleError));
  }

  // Update an existing ticket
  updateTicket(id: number, request: TicketCreateRequest): Observable<TicketResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    const url = `${this.baseUrl}/tickets/${id}`;
    console.log('Updating ticket at URL:', url);
    console.log('Update data:', request);
    return this.http.put<TicketResponse>(url, request, { headers })
      .pipe(catchError(this.handleError));
  }

  // Delete a ticket
  deleteTicket(id: number): Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    return this.http.delete<void>(`${this.baseUrl}/tickets/${id}`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Book a ticket (simplified booking process)
  bookTicket(bookingRequest: TicketBookingRequest): Observable<TicketResponse> {
    // First get the ticket type to calculate total amount
    return this.getTicketTypeById(bookingRequest.ticketTypeId).pipe(
      catchError(this.handleError),
      // Then create the ticket with calculated amount
      switchMap((ticketType: TicketType) => {
        const totalAmount = ticketType.price * bookingRequest.quantity;
        
        const ticketCreateRequest: TicketCreateRequest = {
          userId: bookingRequest.userId || 1, // Default user ID, should come from auth service
          ticketType: {
            ticketTypeId: bookingRequest.ticketTypeId
          },
          validFrom: bookingRequest.visitDate,
          totalAmount: totalAmount,
          paymentStatus: 'PENDING',
          paymentMode: 'CARD'
        };

        return this.createTicket(ticketCreateRequest);
      })
    );
  }

  // Get user tickets (filtered by user ID)
  getUserTickets(userId?: number): Observable<Ticket[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    
    if (userId) {
      return this.http.get<Ticket[]>(`${this.baseUrl}/tickets/user/${userId}`, { headers })
        .pipe(catchError(this.handleError));
    } else {
      // For now, return all tickets if no user ID provided
      // In a real app, you'd have a `/tickets/my` endpoint for current user
      return this.getAllTickets();
    }
  }

  // Get ticket statistics
  getTicketStatistics(): Observable<any> {
    return this.getAllTickets().pipe(
      catchError(this.handleError),
      // Calculate statistics from tickets
    );
  }
}
