import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MaintenanceAlertService, MaintenanceAlertDto, MaintenanceAlertCreateDto, AlertType, Priority } from '../../../services/maintenance-alert.service';
import { RideService, RideDto } from '../../../services/ride.service';

@Component({
  selector: 'app-maintenance-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './maintenance-management.component.html',
  styleUrls: ['./maintenance-management.component.css']
})
export class MaintenanceManagementComponent implements OnInit {
  alerts: MaintenanceAlertDto[] = [];
  rides: RideDto[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  form!: FormGroup;
  editId: number | null = null;

  alertTypes: AlertType[] = ['SCHEDULED_MAINTENANCE', 'EMERGENCY_CLOSURE', 'WEATHER_CLOSURE', 'INSPECTION'];
  priorities: Priority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

  constructor(
    private maintenanceService: MaintenanceAlertService,
    private rideService: RideService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      rideId: [null, [Validators.required]],
      alertType: ['SCHEDULED_MAINTENANCE' as AlertType, [Validators.required]],
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      description: ['',[Validators.maxLength(1000)]],
      startTime: ['', [Validators.required]],
      endTime: [''],
      priority: ['MEDIUM' as Priority, [Validators.required]],
      createdBy: ['']
    });
    this.load();
  }

  load(): void {
    this.loading = true;
    // Load active alerts
    this.maintenanceService.getAllActiveAlerts().subscribe({
      next: (res) => { this.alerts = res; this.loading = false; },
      error: (err) => { this.errorMessage = err.message || 'Failed to load alerts'; this.loading = false; }
    });
    // Load rides for dropdown (first page large size)
    this.rideService.getRides({ page: 0, size: 200, sort: 'name', direction: 'asc' }).subscribe({
      next: (page) => { this.rides = page.content; },
      error: () => {}
    });
  }

  submit(): void {
    if (this.form.invalid) {
      console.log('Form is invalid:', this.form.errors);
      return;
    }
    
    const formValue = this.form.value;
    
    // Additional validation
    if (!formValue.rideId) {
      this.errorMessage = 'Please select a ride';
      return;
    }
    
    if (!formValue.startTime) {
      this.errorMessage = 'Please select a start time';
      return;
    }
    
    // Check if start time is in the past (backend validation)
    const startTime = new Date(formValue.startTime);
    const now = new Date();
    if (startTime < now) {
      this.errorMessage = 'Start time cannot be in the past';
      return;
    }
    
    // Check if selected ride exists
    const selectedRide = this.rides.find(r => r.id === formValue.rideId);
    if (!selectedRide) {
      this.errorMessage = 'Selected ride not found';
      return;
    }
    
    this.errorMessage = '';
    this.successMessage = '';
    
    // Convert datetime-local values to backend expected format 'yyyy-MM-dd HH:mm:ss'
    
    // Helper function to format date to backend expected format
    const formatDateForBackend = (dateString: string): string => {
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    };
    
    const payload: MaintenanceAlertCreateDto = {
      rideId: Number(formValue.rideId), // Ensure it's a number
      alertType: formValue.alertType as AlertType,
      title: formValue.title,
      description: formValue.description || undefined,
      startTime: formatDateForBackend(formValue.startTime),
      endTime: formValue.endTime ? formatDateForBackend(formValue.endTime) : undefined,
      priority: formValue.priority as Priority,
      createdBy: formValue.createdBy || undefined
    };
    
    console.log('Form values:', formValue); // Debug log
    console.log('Selected ride:', selectedRide); // Debug log
    console.log('Sending payload:', payload); // Debug log
    console.log('Payload rideId type:', typeof payload.rideId); // Debug log
    console.log('Payload startTime:', payload.startTime); // Debug log
    
    this.loading = true;
    if (this.editId) {
      this.maintenanceService.updateAlert(this.editId, payload).subscribe({
        next: () => { this.successMessage = 'Alert updated'; this.cancelEdit(); this.load(); },
        error: (err) => { 
          console.error('Update error:', err); // Debug log
          console.error('Error status:', err.status); // Debug log
          console.error('Error message:', err.message); // Debug log
          console.error('Error details:', err.error); // Debug log
          
          if (err.status === 500) {
            this.errorMessage = `Server error (500): ${err.error?.message || err.message || 'Internal server error'}`;
          } else if (err.status === 400) {
            this.errorMessage = `Bad request (400): ${err.error?.message || err.message || 'Invalid data provided'}`;
          } else if (err.status === 401) {
            this.errorMessage = 'Unauthorized (401): Please check your authentication';
          } else if (err.status === 403) {
            this.errorMessage = 'Forbidden (403): You do not have permission to perform this action';
          } else if (err.status === 404) {
            this.errorMessage = 'Not found (404): The requested resource was not found';
          } else {
            this.errorMessage = `Error ${err.status || 'Unknown'}: ${err.error?.message || err.message || 'An error occurred'}`;
          }
          
          this.loading = false; 
        }
      });
    } else {
      this.maintenanceService.createAlert(payload).subscribe({
        next: () => { this.successMessage = 'Alert created'; this.form.reset({ alertType: 'SCHEDULED_MAINTENANCE', priority: 'MEDIUM' }); this.load(); },
        error: (err) => { 
          console.error('Create error:', err); // Debug log
          console.error('Error status:', err.status); // Debug log
          console.error('Error message:', err.message); // Debug log
          console.error('Error details:', err.error); // Debug log
          
          if (err.status === 500) {
            this.errorMessage = `Server error (500): ${err.error?.message || err.message || 'Internal server error'}`;
          } else if (err.status === 400) {
            this.errorMessage = `Bad request (400): ${err.error?.message || err.message || 'Invalid data provided'}`;
          } else if (err.status === 401) {
            this.errorMessage = 'Unauthorized (401): Please check your authentication';
          } else if (err.status === 403) {
            this.errorMessage = 'Forbidden (403): You do not have permission to perform this action';
          } else if (err.status === 404) {
            this.errorMessage = 'Not found (404): The requested resource was not found';
          } else {
            this.errorMessage = `Error ${err.status || 'Unknown'}: ${err.error?.message || err.message || 'An error occurred'}`;
          }
          
          this.loading = false; 
        }
      });
    }
  }

  startEdit(alert: MaintenanceAlertDto): void {
    this.editId = alert.id;
    this.form.patchValue({
      rideId: alert.rideId,
      alertType: alert.alertType,
      title: alert.title,
      description: alert.description || '',
      startTime: alert.startTime,
      endTime: alert.endTime || '',
      priority: alert.priority,
      createdBy: alert.createdBy || ''
    });
  }

  cancelEdit(): void {
    this.editId = null;
    this.form.reset({ alertType: 'SCHEDULED_MAINTENANCE', priority: 'MEDIUM' });
  }

  toggleActive(alert: MaintenanceAlertDto): void {
    this.maintenanceService.updateAlertStatus(alert.id, !alert.isActive).subscribe({
      next: (updated) => { alert.isActive = updated.isActive; },
      error: (err) => { this.errorMessage = err.message || 'Failed to update status'; }
    });
  }

  remove(id: number): void {
    if (!confirm('Delete this alert?')) return;
    this.maintenanceService.deleteAlert(id).subscribe({
      next: () => { this.successMessage = 'Alert deleted'; this.load(); },
      error: (err) => { this.errorMessage = err.message || 'Delete failed'; }
    });
  }
}


