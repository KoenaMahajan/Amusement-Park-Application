import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { RideService, RideDto } from '../../../../services/ride.service';
import { FavoritesService, FavoriteDto } from '../../../../services/favorites.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-ride-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-detail.component.html',
  styleUrls: ['./ride-detail.component.css']
})
export class RideDetailComponent implements OnInit {
  ride?: RideDto;
  loading = false;
  errorMessage = '';
  isFavorite = false;
  favoriteId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private rideService: RideService,
    private favoritesService: FavoritesService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.fetch(id);
    } else {
      this.errorMessage = 'Invalid ride id';
    }
  }

  fetch(id: number) {
    this.loading = true;
    this.rideService.getRideById(id).subscribe({
      next: (r) => {
        this.ride = r;
        this.loading = false;
        this.refreshFavoriteState();
      },
      error: (err) => { this.errorMessage = err.message || 'Failed to load ride'; this.loading = false; }
    });
  }

  refreshFavoriteState() {
    const user = this.authService.getCurrentUser();
    if (!user || !this.ride) return;
    this.favoritesService.checkFavorite(Number(user.id), this.ride.id).subscribe({
      next: (res: any) => {
        this.isFavorite = Boolean(res?.isFavorite);
        this.favoriteId = res?.favorite?.id ?? null;
      },
      error: () => {}
    });
  }

  toggleFavorite() {
    const user = this.authService.getCurrentUser();
    if (!user || !this.ride) return;
    if (this.isFavorite) {
      if (this.favoriteId) {
        this.favoritesService.removeFavoriteById(this.favoriteId).subscribe({
          next: () => { this.isFavorite = false; this.favoriteId = null; },
          error: () => {}
        });
      } else {
        this.favoritesService.removeFavorite(Number(user.id), this.ride.id).subscribe({
          next: () => { this.isFavorite = false; this.favoriteId = null; },
          error: () => {}
        });
      }
    } else {
      this.favoritesService.addToFavorites({ userId: Number(user.id), rideId: this.ride.id }).subscribe({
        next: (fav) => { this.isFavorite = true; this.favoriteId = fav.id; },
        error: () => {}
      });
    }
  }
}


