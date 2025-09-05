import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TicketService, TicketType, Ticket, TicketCreateRequest } from '../../../services/ticket.service';
import { MembershipService, MembershipPlan } from '../../../services/membership.service';

@Component({
  selector: 'app-ticket-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './ticket-management.component.html',
  styleUrls: ['./ticket-management.component.css']
})
export class TicketManagementComponent implements OnInit {
  ticketTypes: TicketType[] = [];
  tickets: Ticket[] = [];
  membershipPlans: MembershipPlan[] = [];
  isLoading = false;
  message = '';
  messageType = '';
  showAddForm = false;
  showTicketForm = false;
  editingTicketType: TicketType | null = null;
  editingTicket: Ticket | null = null;
  ticketTypeForm: FormGroup;
  ticketForm: FormGroup;
  activeTab = 'ticket-types'; // 'ticket-types' or 'tickets'
  paymentModes = ['UPI', 'CARD', 'WALLET', 'CASH'];
  paymentStatuses = ['PENDING', 'PAID', 'FAILED'];

  constructor(
    private ticketService: TicketService,
    private membershipService: MembershipService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.ticketTypeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: ['', [Validators.required, Validators.min(0)]],
      validityDays: ['', [Validators.required, Validators.min(1)]],
      isVip: [false]
    });

    this.ticketForm = this.fb.group({
      userId: ['', [Validators.required, Validators.min(1)]],
      ticketTypeId: ['', [Validators.required]],
      validFrom: ['', [Validators.required]],
      totalAmount: ['', [Validators.required, Validators.min(0)]],
      paymentStatus: ['PENDING', [Validators.required]],
      paymentMode: ['CARD', [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadTicketTypes();
    this.loadTickets();
    this.loadMembershipPlans();
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

  loadTickets() {
    this.ticketService.getAllTickets().subscribe({
      next: (tickets) => {
        this.tickets = tickets;
      },
      error: (error) => {
        console.error('Failed to load tickets:', error);
      }
    });
  }

  loadMembershipPlans() {
    this.membershipService.getAllMembershipPlans().subscribe({
      next: (plans) => {
        this.membershipPlans = plans;
      },
      error: (error) => {
        console.error('Failed to load membership plans:', error);
      }
    });
  }

  showAddTicketTypeForm() {
    this.showAddForm = true;
    this.showTicketForm = false;
    this.editingTicketType = null;
    this.editingTicket = null;
    this.ticketTypeForm.reset({ isVip: false });
  }

  showAddTicketForm() {
    this.showTicketForm = true;
    this.showAddForm = false;
    this.editingTicketType = null;
    this.editingTicket = null;
    this.ticketForm.reset({
      paymentStatus: 'PENDING',
      paymentMode: 'CARD'
    });
  }

  editTicketType(ticketType: TicketType) {
    console.log('Editing ticket type:', ticketType);
    this.editingTicketType = ticketType;
    this.showAddForm = true;
    this.showTicketForm = false;
    this.editingTicket = null;
    this.ticketTypeForm.patchValue({
      name: ticketType.name,
      description: ticketType.description,
      price: ticketType.price,
      validityDays: ticketType.validityDays,
      isVip: ticketType.isVip
    });
    // Mark all controls as touched to trigger validation
    Object.keys(this.ticketTypeForm.controls).forEach(key => {
      this.ticketTypeForm.get(key)?.markAsTouched();
    });
    console.log('Form after patch:', this.ticketTypeForm.value);
    console.log('Form valid:', this.ticketTypeForm.valid);
  }

  editTicket(ticket: Ticket) {
    console.log('Editing ticket:', ticket);
    this.editingTicket = ticket;
    this.showTicketForm = true;
    this.showAddForm = false;
    this.editingTicketType = null;
    const validFromInput = ticket.validFrom ? ('' + ticket.validFrom).split('T')[0] : '';
    console.log('Valid from input:', validFromInput);
    this.ticketForm.patchValue({
      userId: ticket.userId,
      ticketTypeId: ticket.ticketType.ticketTypeId,
      validFrom: validFromInput,
      totalAmount: ticket.totalAmount,
      paymentStatus: ticket.paymentStatus,
      paymentMode: ticket.paymentMode
    });
    // Mark all controls as touched to trigger validation
    Object.keys(this.ticketForm.controls).forEach(key => {
      this.ticketForm.get(key)?.markAsTouched();
    });
    console.log('Ticket form after patch:', this.ticketForm.value);
    console.log('Ticket form valid:', this.ticketForm.valid);
  }

  cancelForm() {
    this.showAddForm = false;
    this.showTicketForm = false;
    this.editingTicketType = null;
    this.editingTicket = null;
    this.ticketTypeForm.reset();
    this.ticketForm.reset();
  }

  onSubmitTicketType() {
    console.log('=== TICKET TYPE FORM SUBMISSION ===');
    console.log('Form valid:', this.ticketTypeForm.valid);
    console.log('Form errors:', this.ticketTypeForm.errors);
    console.log('Form value:', this.ticketTypeForm.value);
    
    // Check each form control
    Object.keys(this.ticketTypeForm.controls).forEach(key => {
      const control = this.ticketTypeForm.get(key);
      console.log(`Control ${key}:`, {
        value: control?.value,
        valid: control?.valid,
        errors: control?.errors,
        touched: control?.touched,
        dirty: control?.dirty
      });
    });
    
    if (this.ticketTypeForm.valid) {
      const ticketTypeData = this.ticketTypeForm.value;
      console.log('Submitting ticket type data:', ticketTypeData);
      console.log('Editing ticket type:', this.editingTicketType);
      
      if (this.editingTicketType) {
        // Update existing ticket type
        console.log('Updating ticket type with ID:', this.editingTicketType.ticketTypeId);
        this.ticketService.updateTicketType(this.editingTicketType.ticketTypeId!, ticketTypeData).subscribe({
          next: (response) => {
            console.log('Update successful:', response);
            this.message = 'Ticket type updated successfully';
            this.messageType = 'success';
            this.loadTicketTypes();
            this.cancelForm();
          },
          error: (error) => {
            console.error('Update failed:', error);
            this.message = error.message || 'Failed to update ticket type';
            this.messageType = 'error';
          }
        });
      } else {
        // Add new ticket type
        console.log('Adding new ticket type');
        this.ticketService.addTicketType(ticketTypeData).subscribe({
          next: (response) => {
            console.log('Add successful:', response);
            this.message = 'Ticket type added successfully';
            this.messageType = 'success';
            this.loadTicketTypes();
            this.cancelForm();
          },
          error: (error) => {
            console.error('Add failed:', error);
            this.message = error.message || 'Failed to add ticket type';
            this.messageType = 'error';
          }
        });
      }
    } else {
      console.log('Form is invalid');
      this.message = 'Please fix form errors before submitting';
      this.messageType = 'error';
    }
  }

  onSubmitTicket() {
    console.log('=== TICKET FORM SUBMISSION ===');
    console.log('Ticket form valid:', this.ticketForm.valid);
    console.log('Ticket form errors:', this.ticketForm.errors);
    console.log('Ticket form value:', this.ticketForm.value);
    
    // Check each form control
    Object.keys(this.ticketForm.controls).forEach(key => {
      const control = this.ticketForm.get(key);
      console.log(`Control ${key}:`, {
        value: control?.value,
        valid: control?.valid,
        errors: control?.errors,
        touched: control?.touched,
        dirty: control?.dirty
      });
    });
    
    if (this.ticketForm.valid) {
      const ticketData = this.ticketForm.value;
      const ticketCreateRequest: TicketCreateRequest = {
        userId: ticketData.userId,
        ticketType: {
          ticketTypeId: ticketData.ticketTypeId
        },
        validFrom: ticketData.validFrom,
        totalAmount: ticketData.totalAmount,
        paymentStatus: ticketData.paymentStatus,
        paymentMode: ticketData.paymentMode
      };
      
      console.log('Submitting ticket data:', ticketCreateRequest);
      console.log('Editing ticket:', this.editingTicket);
      
      if (this.editingTicket) {
        // Update existing ticket
        console.log('Updating ticket with ID:', this.editingTicket.ticketId);
        this.ticketService.updateTicket(this.editingTicket.ticketId!, ticketCreateRequest).subscribe({
          next: (response) => {
            console.log('Ticket update successful:', response);
            this.message = 'Ticket updated successfully';
            this.messageType = 'success';
            this.loadTickets();
            this.cancelForm();
          },
          error: (error) => {
            console.error('Ticket update failed:', error);
            this.message = error.message || 'Failed to update ticket';
            this.messageType = 'error';
          }
        });
      } else {
        // Create new ticket
        console.log('Creating new ticket');
        this.ticketService.createTicket(ticketCreateRequest).subscribe({
          next: (response) => {
            console.log('Ticket creation successful:', response);
            this.message = 'Ticket created successfully';
            this.messageType = 'success';
            this.loadTickets();
            this.cancelForm();
          },
          error: (error) => {
            console.error('Ticket creation failed:', error);
            this.message = error.message || 'Failed to create ticket';
            this.messageType = 'error';
          }
        });
      }
    } else {
      console.log('Ticket form is invalid');
      this.message = 'Please fix form errors before submitting';
      this.messageType = 'error';
    }
  }

  deleteTicketType(ticketType: TicketType) {
    if (confirm(`Are you sure you want to delete the "${ticketType.name}" ticket type?`)) {
      this.ticketService.deleteTicketType(ticketType.ticketTypeId!).subscribe({
        next: (response) => {
          this.message = 'Ticket type deleted successfully';
          this.messageType = 'success';
          this.loadTicketTypes();
        },
        error: (error) => {
          this.message = error.message || 'Failed to delete ticket type';
          this.messageType = 'error';
        }
      });
    }
  }

  deleteTicket(ticket: Ticket) {
    if (confirm(`Are you sure you want to delete this ticket (${ticket.ticketCode})?`)) {
      this.ticketService.deleteTicket(ticket.ticketId!).subscribe({
        next: (response) => {
          this.message = 'Ticket deleted successfully';
          this.messageType = 'success';
          this.loadTickets();
        },
        error: (error) => {
          this.message = error.message || 'Failed to delete ticket';
          this.messageType = 'error';
        }
      });
    }
  }

  getTicketsCountForType(typeId: number): number {
    return this.tickets.filter(ticket => ticket.ticketType.ticketTypeId === typeId).length;
  }

  getTicketTypeStatistics(typeId: number) {
    const typeTickets = this.tickets.filter(ticket => ticket.ticketType.ticketTypeId === typeId);
    const paidTickets = typeTickets.filter(t => t.paymentStatus === 'PAID');
    const pendingTickets = typeTickets.filter(t => t.paymentStatus === 'PENDING');
    const failedTickets = typeTickets.filter(t => t.paymentStatus === 'FAILED');
    
    return {
      total: typeTickets.length,
      paid: paidTickets.length,
      pending: pendingTickets.length,
      failed: failedTickets.length,
      revenue: paidTickets.reduce((total, t) => total + t.totalAmount, 0)
    };
  }

  getTotalRevenue(): number {
    return this.tickets
      .filter(t => t.paymentStatus === 'PAID')
      .reduce((total, ticket) => total + ticket.totalAmount, 0);
  }

  getPaidTicketsCount(): number {
    return this.tickets.filter(t => t.paymentStatus === 'PAID').length;
  }

  getPendingTicketsCount(): number {
    return this.tickets.filter(t => t.paymentStatus === 'PENDING').length;
  }

  getFailedTicketsCount(): number {
    return this.tickets.filter(t => t.paymentStatus === 'FAILED').length;
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  navigateToAdminDashboard() {
    this.router.navigate(['/admin/dashboard']);
  }

  getErrorMessage(field: string): string {
    const control = this.ticketTypeForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return `${field} is required.`;
      if (control.errors['minlength']) {
        if (field === 'name') return 'Name must be at least 2 characters.';
        return 'Description must be at least 10 characters.';
      }
      if (control.errors['min']) {
        if (field === 'price') return 'Price must be greater than 0.';
        return 'Validity days must be at least 1.';
      }
    }
    return '';
  }

  getTicketErrorMessage(field: string): string {
    const control = this.ticketForm.get(field);
    if (control?.errors) {
      if (control.errors['required']) return `${field} is required.`;
      if (control.errors['min']) {
        if (field === 'userId') return 'User ID must be greater than 0.';
        return 'Amount must be greater than 0.';
      }
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

  onTicketTypeChange() {
    const ticketTypeId = this.ticketForm.get('ticketTypeId')?.value;
    if (ticketTypeId) {
      const selectedType = this.ticketTypes.find(type => type.ticketTypeId === ticketTypeId);
      if (selectedType) {
        this.ticketForm.patchValue({
          totalAmount: selectedType.price
        });
      }
    }
  }
}
