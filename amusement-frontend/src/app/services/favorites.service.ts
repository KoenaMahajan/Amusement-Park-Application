import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideDto } from './ride.service';

export interface FavoriteDto {
  id: number;
  userId: number;
  ride: RideDto;
  notes?: string;
  addedAt?: string;
}

export interface FavoriteCreateDto {
  userId: number;
  rideId: number;
  notes?: string;
}

export interface FavoriteCheckResponse {
  isFavorite: boolean;
  favorite: FavoriteDto | null;
}

@Injectable({ providedIn: 'root' })
export class FavoritesService {
  private baseUrl = '/api/favorites';

  constructor(private http: HttpClient) {}

  // Get all favorite rides for a user
  getUserFavorites(userId: number): Observable<FavoriteDto[]> {
    return this.http.get<FavoriteDto[]>(`${this.baseUrl}/user/${userId}`);
  }

  // Add ride to user's favorites
  addToFavorites(payload: FavoriteCreateDto): Observable<FavoriteDto> {
    return this.http.post<FavoriteDto>(this.baseUrl, payload);
  }

  // Remove specific favorite by ID
  removeFavoriteById(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.baseUrl}/${id}`);
  }

  // Remove ride from user's favorites
  removeFavorite(userId: number, rideId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.baseUrl}/user/${userId}/ride/${rideId}`);
  }

  // Check if ride is in user's favorites
  checkFavorite(userId: number, rideId: number): Observable<FavoriteCheckResponse> {
    return this.http.get<FavoriteCheckResponse>(`${this.baseUrl}/user/${userId}/ride/${rideId}`);
  }

  // Update favorite notes
  updateFavoriteNotes(id: number, notes: string): Observable<FavoriteDto> {
    return this.http.put<FavoriteDto>(`${this.baseUrl}/${id}`, { notes });
  }

  // Get most popular rides (analytics) - Admin only
  getMostPopularRides(limit: number = 10): Observable<RideDto[]> {
    const params = new HttpParams().set('limit', limit);
    return this.http.get<RideDto[]>(`${this.baseUrl}/popular`, { params });
  }

  // Get favorites count for a user
  getUserFavoritesCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/user/${userId}/count`);
  }

  // Get favorites count for a ride - Admin only
  getRideFavoritesCount(rideId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/ride/${rideId}/count`);
  }

  // Get users who favorited a specific ride - Admin only
  getUsersWhoFavoritedRide(rideId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.baseUrl}/ride/${rideId}/users`);
  }

  // Get favorites with notes for a user
  getUserFavoritesWithNotes(userId: number): Observable<FavoriteDto[]> {
    return this.http.get<FavoriteDto[]>(`${this.baseUrl}/user/${userId}/with-notes`);
  }

  // Get favorites statistics - Admin only
  getFavoritesStatistics(): Observable<{ totalUsers: number; totalFavorites: number; avgFavoritesPerUser: number }> {
    return this.http.get<{ totalUsers: number; totalFavorites: number; avgFavoritesPerUser: number }>(`${this.baseUrl}/statistics`);
  }
}


