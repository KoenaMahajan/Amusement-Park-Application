import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface ChatbotKeyword {
  id?: number;
  keyword: string;
  response: string;
}

@Injectable({ providedIn: 'root' })
export class ChatbotService {
  private baseUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check if the backend is running.';
      } else if (error.status === 404) {
        errorMessage = 'Chatbot endpoint not found. Backend chatbot functionality not implemented yet.';
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else {
        errorMessage = `Server error: ${error.status}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }

  getAll(): Observable<ChatbotKeyword[]> {
    return this.http
      .get<ChatbotKeyword[]>(`${this.baseUrl}/admin/chatbot/all`)
      .pipe(catchError(this.handleError));
  }

  add(keyword: ChatbotKeyword): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http
      .post(`${this.baseUrl}/admin/chatbot/add`, keyword, { headers, responseType: 'text' })
      .pipe(catchError(this.handleError));
  }

  addAll(keywords: ChatbotKeyword[]): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http
      .post(`${this.baseUrl}/admin/chatbot/add-all`, keywords, { headers, responseType: 'text' })
      .pipe(catchError(this.handleError));
  }

  update(id: number, keyword: ChatbotKeyword): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http
      .put(`${this.baseUrl}/admin/chatbot/update/${id}`, keyword, { headers, responseType: 'text' })
      .pipe(catchError(this.handleError));
  }

  delete(id: number): Observable<string> {
    return this.http
      .delete(`${this.baseUrl}/admin/chatbot/delete/${id}`, { responseType: 'text' })
      .pipe(catchError(this.handleError));
  }
}


