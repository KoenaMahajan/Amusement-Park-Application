import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-test-connection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './test-connection.component.html',
  styleUrls: ['./test-connection.component.css']
})
export class TestConnectionComponent implements OnInit {
  connectionStatus = 'Testing...';
  isConnected = false;
  isLoading = true;
  errorMessage = '';
  ticketTypesCount = 0;
  ticketsCount = 0;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.testConnection();
  }

  testConnection() {
    this.isLoading = true;
    this.connectionStatus = 'Testing connection...';
    this.errorMessage = '';

    // Test multiple endpoints
    Promise.all([
      this.testMembershipPlans(),
      this.testTicketTypes(),
      this.testTickets()
    ]).then(() => {
      this.isConnected = true;
      this.connectionStatus = 'All APIs connected successfully!';
      this.isLoading = false;
    }).catch((error) => {
      this.isConnected = false;
      this.connectionStatus = 'Connection failed';
      this.errorMessage = this.getErrorMessage(error);
      this.isLoading = false;
    });
  }

  private testMembershipPlans(): Promise<void> {
    return this.http.get('/api/membership-plans', { observe: 'response' })
      .toPromise()
      .then(() => {
        console.log('Membership plans API: OK');
      })
      .catch((error) => {
        console.error('Membership plans API failed:', error);
        throw error;
      });
  }

  private testTicketTypes(): Promise<void> {
    return this.http.get('/ticket-types', { observe: 'response' })
      .toPromise()
      .then((response: any) => {
        console.log('Ticket types API: OK');
        this.ticketTypesCount = response.body?.length || 0;
      })
      .catch((error) => {
        console.error('Ticket types API failed:', error);
        throw error;
      });
  }

  private testTickets(): Promise<void> {
    return this.http.get('/tickets', { observe: 'response' })
      .toPromise()
      .then((response: any) => {
        console.log('Tickets API: OK');
        this.ticketsCount = response.body?.length || 0;
      })
      .catch((error) => {
        console.error('Tickets API failed:', error);
        throw error;
      });
  }

  private getErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Unable to connect to server. Please check if the backend is running on port 8082 and the proxy is configured correctly.';
    } else if (error.status === 404) {
      return 'API endpoint not found. Please check the backend URL.';
    } else if (error.status === 403) {
      return 'Access forbidden. CORS issue detected.';
    } else {
      return error.error || `Server error: ${error.status}`;
    }
  }

  retryConnection() {
    this.testConnection();
  }
}
