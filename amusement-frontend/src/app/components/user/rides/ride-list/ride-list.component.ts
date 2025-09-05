import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RideService, RideDto, ThrillLevel, Page } from '../../../../services/ride.service';
import { FavoritesService, FavoriteDto } from '../../../../services/favorites.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-ride-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ride-list.component.html',
  styleUrls: ['./ride-list.component.css']
})
export class RideListComponent implements OnInit {
  rides: RideDto[] = [];
  loading = false;
  errorMessage = '';
  userFavorites: Map<number, boolean> = new Map(); // Track favorite state for each ride

  // filters
  keyword = '';
  thrillLevel: ThrillLevel | '' = '';
  minAge: number | null = null;
  operational: boolean | '' = '';

  // pagination
  page = 0;
  size = 10;
  totalPages = 0;

  thrillLevels: ThrillLevel[] = ['LOW', 'MEDIUM', 'HIGH', 'EXTREME'];

  constructor(
    private rideService: RideService, 
    private router: Router,
    private favoritesService: FavoritesService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.rideService.searchRides({
      keyword: this.keyword || null,
      thrillLevel: (this.thrillLevel as ThrillLevel) || null,
      suitableForAge: this.minAge ?? null,
      operational: this.operational === '' ? null : Boolean(this.operational),
      page: this.page,
      size: this.size,
      sort: 'name',
      direction: 'asc'
    }).subscribe({
      next: (res: Page<RideDto>) => {
        this.rides = res.content;
        this.totalPages = res.totalPages;
        this.loading = false;
        this.loadUserFavorites();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load rides';
        this.loading = false;
      }
    });
  }

  loadUserFavorites(): void {
    const user = this.authService.getCurrentUser();
    if (!user) return;

    this.rides.forEach(ride => {
      this.favoritesService.checkFavorite(Number(user.id), ride.id).subscribe({
        next: (res) => {
          this.userFavorites.set(ride.id, res.isFavorite);
        },
        error: () => {
          this.userFavorites.set(ride.id, false);
        }
      });
    });
  }

  toggleFavorite(ride: RideDto): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage = 'Please log in to manage favorites';
      return;
    }

    const isCurrentlyFavorite = this.userFavorites.get(ride.id) || false;

    if (isCurrentlyFavorite) {
      // Remove from favorites
      this.favoritesService.removeFavorite(Number(user.id), ride.id).subscribe({
        next: () => {
          this.userFavorites.set(ride.id, false);
          // Update the ride's favorites count
          if (ride.favoritesCount && ride.favoritesCount > 0) {
            ride.favoritesCount--;
          }
        },
        error: (err) => {
          this.errorMessage = err.message || 'Failed to remove from favorites';
        }
      });
    } else {
      // Add to favorites
      this.favoritesService.addToFavorites({
        userId: Number(user.id),
        rideId: ride.id
      }).subscribe({
        next: () => {
          this.userFavorites.set(ride.id, true);
          // Update the ride's favorites count
          if (ride.favoritesCount !== undefined) {
            ride.favoritesCount = (ride.favoritesCount || 0) + 1;
          }
        },
        error: (err) => {
          this.errorMessage = err.message || 'Failed to add to favorites';
        }
      });
    }
  }

  isFavorite(rideId: number): boolean {
    return this.userFavorites.get(rideId) || false;
  }

  clearFilters(): void {
    this.keyword = '';
    this.thrillLevel = '';
    this.minAge = null;
    this.operational = '';
    this.page = 0;
    this.load();
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page += 1;
      this.load();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page -= 1;
      this.load();
    }
  }
}


