import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RideService, RideDto, RideCreateDto, RideUpdateDto, ThrillLevel, Page } from '../../../services/ride.service';
import { FavoritesService } from '../../../services/favorites.service';

@Component({
  selector: 'app-ride-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ride-management.component.html',
  styleUrls: ['./ride-management.component.css']
})
export class RideManagementComponent implements OnInit {
  rides: RideDto[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  page = 0;
  size = 10;
  totalPages = 0;

  thrillLevels: ThrillLevel[] = ['LOW', 'MEDIUM', 'HIGH', 'EXTREME'];

  form!: FormGroup;
  editId: number | null = null;

  constructor(
    private rideService: RideService, 
    private fb: FormBuilder,
    private favoritesService: FavoritesService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(1000)]],
      thrillLevel: ['MEDIUM', [Validators.required]],
      minAge: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      maxAge: [null],
      durationMinutes: [5, [Validators.required, Validators.min(1), Validators.max(120)]],
      heightRequirementCm: [null],
      photoUrl: [''],
      videoUrl: [''],
      locationDescription: [''],
      safetyInstructions: ['']
    });
    this.load();
  }

  load(): void {
    this.loading = true;
    this.rideService.getRides({ page: this.page, size: this.size, sort: 'name', direction: 'asc' })
      .subscribe({
        next: (res: Page<RideDto>) => { 
          this.rides = res.content; 
          this.totalPages = res.totalPages; 
          this.loading = false;
          this.loadFavoritesCounts();
        },
        error: (err) => { this.errorMessage = err.message || 'Failed to load rides'; this.loading = false; }
      });
  }

  loadFavoritesCounts(): void {
    this.rides.forEach(ride => {
      this.favoritesService.getRideFavoritesCount(ride.id).subscribe({
        next: (res) => {
          ride.favoritesCount = res.count;
        },
        error: () => {
          ride.favoritesCount = 0;
        }
      });
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.errorMessage = '';
    this.successMessage = '';
    const payload = this.form.value as RideCreateDto;

    if (this.editId) {
      this.loading = true;
      this.rideService.updateRide(this.editId, payload as RideUpdateDto).subscribe({
        next: (updated) => {
          this.successMessage = `Updated '${updated.name}'`;
          this.cancelEdit();
          this.load();
        },
        error: (err) => { this.errorMessage = err.message || 'Update failed'; this.loading = false; }
      });
    } else {
      this.loading = true;
      this.rideService.createRide(payload).subscribe({
        next: (created) => {
          this.successMessage = `Created '${created.name}'`;
          this.form.reset({ thrillLevel: 'MEDIUM', minAge: 0, durationMinutes: 5 });
          this.load();
        },
        error: (err) => { this.errorMessage = err.message || 'Create failed'; this.loading = false; }
      });
    }
  }

  startEdit(ride: RideDto): void {
    this.editId = ride.id;
    this.form.patchValue({
      name: ride.name,
      description: ride.description,
      thrillLevel: ride.thrillLevel,
      minAge: ride.minAge ?? 0,
      maxAge: ride.maxAge ?? null,
      durationMinutes: ride.durationMinutes,
      heightRequirementCm: ride.heightRequirementCm ?? null,
      photoUrl: ride.photoUrl ?? '',
      videoUrl: ride.videoUrl ?? '',
      locationDescription: ride.locationDescription ?? '',
      safetyInstructions: ride.safetyInstructions ?? ''
    });
  }

  cancelEdit(): void {
    this.editId = null;
    this.form.reset({ thrillLevel: 'MEDIUM', minAge: 0, durationMinutes: 5 });
  }

  remove(id: number): void {
    if (!confirm('Delete this ride?')) return;
    this.loading = true;
    this.rideService.deleteRide(id).subscribe({
      next: () => { this.successMessage = 'Ride deleted'; this.load(); },
      error: (err) => { this.errorMessage = err.message || 'Delete failed'; this.loading = false; }
    });
  }

  toggleStatus(ride: RideDto): void {
    this.rideService.updateRideStatus(ride.id, !ride.isOperational).subscribe({
      next: (updated) => { ride.isOperational = updated.isOperational; },
      error: (err) => { this.errorMessage = err.message || 'Failed to update status'; }
    });
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}


