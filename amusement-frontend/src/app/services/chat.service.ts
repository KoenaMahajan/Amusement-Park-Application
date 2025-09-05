import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface ChatMessageDTO {
  userMessage: string;
  botReply: string;
  createdAt: string;
}

export interface ChatMessageRequest { message: string; }

@Injectable({ providedIn: 'root' })
export class ChatService {
   private baseUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else {
        errorMessage = `Server error: ${error.status}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }

  sendMessage(message: string): Observable<{ reply: string }> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http
      .post<{ reply: string }>(`${this.baseUrl}/user/chat`, { message }, { headers })
      .pipe(catchError(this.handleError));
  }

  getHistory(): Observable<ChatMessageDTO[]> {
    return this.http
      .get<ChatMessageDTO[]>(`${this.baseUrl}/user/chathistory`)
      .pipe(catchError(this.handleError));
  }
}


