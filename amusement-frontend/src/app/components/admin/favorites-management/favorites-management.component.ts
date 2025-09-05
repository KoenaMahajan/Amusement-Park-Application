import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FavoritesService, FavoriteDto } from '../../../services/favorites.service';
import { RideService, RideDto } from '../../../services/ride.service';

@Component({
  selector: 'app-favorites-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './favorites-management.component.html',
  styleUrls: ['./favorites-management.component.css']
})
export class FavoritesManagementComponent implements OnInit {
  favorites: FavoriteDto[] = [];
  popularRides: RideDto[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';
  
  // Statistics
  totalUsers = 0;
  totalFavorites = 0;
  avgFavoritesPerUser = 0;
  
  // Filter options
  selectedRideId: number | null = null;
  rides: RideDto[] = [];

  constructor(
    private favoritesService: FavoritesService,
    private rideService: RideService
  ) {}

  ngOnInit(): void {
    this.loadRides();
    this.loadStatistics();
    this.loadPopularRides();
  }

  loadRides(): void {
    this.rideService.getRides({ page: 0, size: 200, sort: 'name', direction: 'asc' }).subscribe({
      next: (page) => { this.rides = page.content; },
      error: () => {}
    });
  }

  loadStatistics(): void {
    this.favoritesService.getFavoritesStatistics().subscribe({
      next: (stats) => {
        this.totalUsers = stats.totalUsers;
        this.totalFavorites = stats.totalFavorites;
        this.avgFavoritesPerUser = stats.avgFavoritesPerUser;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load statistics';
      }
    });
  }

  loadPopularRides(): void {
    this.loading = true;
    this.favoritesService.getMostPopularRides(10).subscribe({
      next: (rides) => {
        this.popularRides = rides;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load popular rides';
        this.loading = false;
      }
    });
  }

  onRideChange(): void {
    if (this.selectedRideId) {
      this.loadRideFavorites(this.selectedRideId);
    } else {
      this.favorites = [];
    }
  }

  loadRideFavorites(rideId: number): void {
    this.loading = true;
    this.favoritesService.getUsersWhoFavoritedRide(rideId).subscribe({
      next: (userIds) => {
        // For now, we'll just show the count. In a real app, you might want to fetch user details
        this.favorites = userIds.map(userId => ({
          id: 0, // Placeholder
          userId: userId,
          ride: this.rides.find(r => r.id === rideId) || {} as RideDto,
          addedAt: undefined,
          notes: undefined
        }));
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load ride favorites';
        this.loading = false;
      }
    });
  }

  removeFavorite(fav: FavoriteDto): void {
    if (!confirm(`Remove this favorite?`)) return;
    
    if (fav.id) {
      this.favoritesService.removeFavoriteById(fav.id).subscribe({
        next: () => {
          this.favorites = this.favorites.filter(f => f.id !== fav.id);
          this.successMessage = 'Favorite removed successfully';
          setTimeout(() => this.successMessage = '', 3000);
          this.loadStatistics(); // Refresh statistics
        },
        error: (err) => {
          this.errorMessage = err.message || 'Failed to remove favorite';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    } else if (this.selectedRideId) {
      this.favoritesService.removeFavorite(fav.userId, this.selectedRideId).subscribe({
        next: () => {
          this.favorites = this.favorites.filter(f => f.userId !== fav.userId);
          this.successMessage = 'Favorite removed successfully';
          setTimeout(() => this.successMessage = '', 3000);
          this.loadStatistics(); // Refresh statistics
        },
        error: (err) => {
          this.errorMessage = err.message || 'Failed to remove favorite';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    }
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}
