import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TicketService, TicketType, Ticket, TicketCreateRequest } from '../../../services/ticket.service';
import { MembershipService, UserMembership } from '../../../services/membership.service';

@Component({
  selector: 'app-ticket-booking',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './ticket-booking.component.html',
  styleUrls: ['./ticket-booking.component.css']
})
export class TicketBookingComponent implements OnInit {
  ticketTypes: TicketType[] = [];
  userMemberships: UserMembership[] = [];
  isLoading = false;
  message = '';
  messageType = '';
  showBookingForm = false;
  selectedTicketType: TicketType | null = null;
  bookingForm: FormGroup;
  userTickets: Ticket[] = [];

  constructor(
    private ticketService: TicketService,
    private membershipService: MembershipService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.bookingForm = this.fb.group({
      ticketTypeId: ['', Validators.required],
      visitDate: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
      paymentMode: ['CARD', Validators.required]
    });
  }

  ngOnInit() {
    this.loadTicketTypes();
    this.loadUserMemberships();
    this.loadUserTickets();
  }

  loadTicketTypes() {
    this.isLoading = true;
    this.ticketService.getAllTicketTypes().subscribe({
      next: (types) => {
        this.ticketTypes = types;
        this.isLoading = false;
      },
      error: (error) => {
        this.message = error.message || 'Failed to load ticket types';
        this.messageType = 'error';
        this.isLoading = false;
      }
    });
  }

  loadUserMemberships() {
    this.membershipService.viewMyMemberships().subscribe({
      next: (memberships) => {
        this.userMemberships = memberships;
      },
      error: (error) => {
        console.error('Failed to load user memberships:', error);
      }
    });
  }

  loadUserTickets() {
    this.ticketService.getUserTickets().subscribe({
      next: (tickets) => {
        this.userTickets = tickets;
      },
      error: (error) => {
        console.error('Failed to load user tickets:', error);
      }
    });
  }

  showBookingFormForType(ticketType: TicketType) {
    this.selectedTicketType = ticketType;
    this.showBookingForm = true;
    this.bookingForm.patchValue({
      ticketTypeId: ticketType.ticketTypeId,
      visitDate: this.getMinVisitDate(),
      quantity: 1,
      paymentMode: 'CARD'
    });
  }

  cancelBooking() {
    this.showBookingForm = false;
    this.selectedTicketType = null;
    this.bookingForm.reset();
  }

  onSubmit() {
    if (this.bookingForm.valid && this.selectedTicketType) {
      const formData = this.bookingForm.value;
      const totalAmount = this.selectedTicketType.price * formData.quantity;
      
      const ticketRequest: TicketCreateRequest = {
        userId: 1, // This should come from auth service
        ticketType: {
          ticketTypeId: formData.ticketTypeId
        },
        validFrom: formData.visitDate,
        totalAmount: totalAmount,
        paymentStatus: 'PENDING',
        paymentMode: formData.paymentMode
      };
      
      this.isLoading = true;
      this.ticketService.createTicket(ticketRequest).subscribe({
        next: (ticketResponse) => {
          this.message = `Ticket booked successfully! Ticket Code: ${ticketResponse.ticketCode}`;
          this.messageType = 'success';
          this.loadUserTickets();
          this.cancelBooking();
          this.isLoading = false;
        },
        error: (error) => {
          this.message = error.message || 'Failed to book ticket';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    }
  }

  getMinVisitDate(): string {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }

  canBookTicketType(ticketType: TicketType): boolean {
    // For now, all ticket types are bookable
    // In a real app, you might check membership requirements
    return true;
  }

  getMembershipRequirement(ticketType: TicketType): string {
    if (ticketType.isVip) {
      return 'VIP ticket - Premium experience';
    }
    return 'Regular ticket - Standard access';
  }

  getMembershipStatus(ticketType: TicketType): { hasMembership: boolean; status: string } {
    if (ticketType.isVip) {
      return { hasMembership: true, status: 'VIP Available' };
    } else {
      return { hasMembership: true, status: 'Available' };
    }
  }

  getTotalPrice(ticketType: TicketType, quantity: number): number {
    return ticketType.price * quantity;
  }

  navigateToUserDashboard() {
    this.router.navigate(['/user/dashboard']);
  }

  getErrorMessage(field: string): string {
    const control = this.bookingForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return `${field} is required.`;
      if (control.errors['min']) return 'Quantity must be at least 1.';
      if (control.errors['max']) return 'Quantity cannot exceed 10.';
    }
    return '';
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID': return 'status-confirmed';
      case 'PENDING': return 'status-pending';
      case 'FAILED': return 'status-cancelled';
      default: return 'status-default';
    }
  }
}
