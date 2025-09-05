package com.amusement.amusement_park.controller.attraction;

import com.amusement.amusement_park.dto.attraction.FavoriteCreateDto;
import com.amusement.amusement_park.dto.attraction.FavoriteDto;
import com.amusement.amusement_park.dto.attraction.RideDto;
import com.amusement.amusement_park.service.attraction.FavoriteService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")

public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Get all favorite rides for a user
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDto>> getUserFavorites(@PathVariable Long userId) {
        List<FavoriteDto> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    // Add ride to user's favorites
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping
    public ResponseEntity<FavoriteDto> addToFavorites(@Valid @RequestBody FavoriteCreateDto createDto) {
        FavoriteDto favorite = favoriteService.addToFavorites(createDto);
        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    // Remove specific favorite by ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removeFavoriteById(@PathVariable Long id) {
        favoriteService.removeFavoriteById(id);
        return ResponseEntity.ok(Map.of("message", "Favorite removed successfully"));
    }

    // Remove ride from user's favorites
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/user/{userId}/ride/{rideId}")
    public ResponseEntity<Map<String, String>> removeFavorite(@PathVariable Long userId, @PathVariable Long rideId) {
        favoriteService.removeFavorite(userId, rideId);
        return ResponseEntity.ok(Map.of("message", "Ride removed from favorites successfully"));
    }

    // Check if ride is in user's favorites
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}/ride/{rideId}")
    public ResponseEntity<Map<String, Object>> checkFavorite(@PathVariable Long userId, @PathVariable Long rideId) {
        boolean isFavorite = favoriteService.isRideInFavorites(userId, rideId);
        FavoriteDto favorite = null;

        if (isFavorite) {
            favorite = favoriteService.getFavorite(userId, rideId);
        }

        return ResponseEntity.ok(Map.of(
                "isFavorite", isFavorite,
                "favorite", favorite != null ? favorite : Map.of()));
    }

    // Update favorite notes
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FavoriteDto> updateFavoriteNotes(@PathVariable Long id,
            @RequestBody Map<String, String> notesMap) {
        String notes = notesMap.get("notes");
        FavoriteDto updatedFavorite = favoriteService.updateFavoriteNotes(id, notes);
        return ResponseEntity.ok(updatedFavorite);
    }

    // Get most favorited rides (analytics)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/popular")
    public ResponseEntity<List<RideDto>> getMostPopularRides(@RequestParam(defaultValue = "10") int limit) {
        List<RideDto> popularRides = favoriteService.getMostPopularRides(limit);
        return ResponseEntity.ok(popularRides);
    }

    // Get favorites count for a user
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUserFavoritesCount(@PathVariable Long userId) {
        Long count = favoriteService.getUserFavoritesCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Get favorites count for a ride
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ride/{rideId}/count")
    public ResponseEntity<Map<String, Long>> getRideFavoritesCount(@PathVariable Long rideId) {
        Long count = favoriteService.getRideFavoritesCount(rideId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Get users who favorited a specific ride
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ride/{rideId}/users")
    public ResponseEntity<List<Long>> getUsersWhoFavoritedRide(@PathVariable Long rideId) {
        List<Long> userIds = favoriteService.getUsersWhoFavoritedRide(rideId);
        return ResponseEntity.ok(userIds);
    }

    // Get favorites with notes for a user
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}/with-notes")
    public ResponseEntity<List<FavoriteDto>> getUserFavoritesWithNotes(@PathVariable Long userId) {
        List<FavoriteDto> favorites = favoriteService.getUserFavoritesWithNotes(userId);
        return ResponseEntity.ok(favorites);
    }

    // Get favorites statistics
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFavoritesStatistics() {
        Object[] stats = favoriteService.getFavoritesStatistics();
        if (stats != null && stats.length >= 3) {
            return ResponseEntity.ok(Map.of(
                    "totalUsers", stats[0],
                    "totalFavorites", stats[1],
                    "avgFavoritesPerUser", stats[2]));
        } else {
            return ResponseEntity.ok(Map.of(
                    "totalUsers", 0,
                    "totalFavorites", 0,
                    "avgFavoritesPerUser", 0.0));
        }
    }
}
