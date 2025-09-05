import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FavoritesService, FavoriteDto } from '../../../services/favorites.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-user-favorites',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './user-favorites.component.html',
  styleUrls: ['./user-favorites.component.css']
})
export class UserFavoritesComponent implements OnInit {
  favorites: FavoriteDto[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private favoritesService: FavoritesService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const user = this.authService.getCurrentUser();
    if (!user) { 
      this.errorMessage = 'Please log in to view favorites.'; 
      return; 
    }
    
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.favoritesService.getUserFavorites(Number(user.id)).subscribe({
      next: (list) => { 
        this.favorites = list; 
        this.loading = false; 
      },
      error: (err) => { 
        this.errorMessage = err.message || 'Failed to load favorites'; 
        this.loading = false; 
      }
    });
  }

  removeFavorite(fav: FavoriteDto) {
    if (!confirm(`Remove "${fav.ride.name}" from favorites?`)) return;
    
    this.favoritesService.removeFavoriteById(fav.id).subscribe({
      next: () => { 
        this.favorites = this.favorites.filter(f => f.id !== fav.id);
        this.successMessage = `"${fav.ride.name}" removed from favorites`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => { 
        this.errorMessage = err.message || 'Failed to remove favorite';
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  updateNotes(fav: FavoriteDto, notes: string) {
    this.favoritesService.updateFavoriteNotes(fav.id, notes).subscribe({
      next: (updated) => {
        const index = this.favorites.findIndex(f => f.id === fav.id);
        if (index !== -1) {
          this.favorites[index] = updated;
        }
        this.successMessage = 'Notes updated successfully';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to update notes';
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }
}


