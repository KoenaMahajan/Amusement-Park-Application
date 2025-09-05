package com.amusement.amusement_park.service.attraction;


import com.amusement.amusement_park.dto.attraction.FavoriteCreateDto;
import com.amusement.amusement_park.dto.attraction.FavoriteDto;
import com.amusement.amusement_park.dto.attraction.RideDto;
import com.amusement.amusement_park.entity.attraction.Ride;
import com.amusement.amusement_park.entity.attraction.UserFavorite;
import com.amusement.amusement_park.exception.attraction.FavoriteAlreadyExistsException;
import com.amusement.amusement_park.exception.attraction.FavoriteNotFoundException;
import com.amusement.amusement_park.exception.attraction.RideNotFoundException;
import com.amusement.amusement_park.repository.attraction.RideRepository;
import com.amusement.amusement_park.repository.attraction.UserFavoriteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class FavoriteService {

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideService rideService;

    // Get all favorite rides for a user
    public List<FavoriteDto> getUserFavorites(Long userId) {
        List<UserFavorite> favorites = userFavoriteRepository.findByUserIdOrderByAddedAtDesc(userId);
        return favorites.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Add ride to user's favorites
    public FavoriteDto addToFavorites(FavoriteCreateDto createDto) {
        // Check if ride exists
        Ride ride = rideRepository.findById(createDto.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + createDto.getRideId()));

        // Check if already in favorites
        if (userFavoriteRepository.existsByUserIdAndRideId(createDto.getUserId(), createDto.getRideId())) {
            throw new FavoriteAlreadyExistsException("Ride is already in user's favorites");
        }

        // Create new favorite
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(createDto.getUserId());
        favorite.setRide(ride);
        favorite.setNotes(createDto.getNotes());

        UserFavorite savedFavorite = userFavoriteRepository.save(favorite);
        return convertToDto(savedFavorite);
    }

    // Remove specific favorite by ID
    public void removeFavoriteById(Long id) {
        if (!userFavoriteRepository.existsById(id)) {
            throw new FavoriteNotFoundException("Favorite not found with id: " + id);
        }
        userFavoriteRepository.deleteById(id);
    }

    // Remove ride from user's favorites
    public void removeFavorite(Long userId, Long rideId) {
        UserFavorite favorite = userFavoriteRepository.findByUserIdAndRideId(userId, rideId)
                .orElseThrow(() -> new FavoriteNotFoundException("Favorite not found for user " + userId + " and ride " + rideId));
        
        userFavoriteRepository.delete(favorite);
    }

    // Check if ride is in user's favorites
    public boolean isRideInFavorites(Long userId, Long rideId) {
        return userFavoriteRepository.existsByUserIdAndRideId(userId, rideId);
    }

    // Get specific favorite
    public FavoriteDto getFavorite(Long userId, Long rideId) {
        UserFavorite favorite = userFavoriteRepository.findByUserIdAndRideId(userId, rideId)
                .orElseThrow(() -> new FavoriteNotFoundException("Favorite not found for user " + userId + " and ride " + rideId));
        
        return convertToDto(favorite);
    }

    // Update favorite notes
    public FavoriteDto updateFavoriteNotes(Long id, String notes) {
        UserFavorite favorite = userFavoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException("Favorite not found with id: " + id));
        
        favorite.setNotes(notes);
        UserFavorite savedFavorite = userFavoriteRepository.save(favorite);
        return convertToDto(savedFavorite);
    }

    // Get most popular rides (most favorited)
    public List<RideDto> getMostPopularRides(int limit) {
        return rideService.getMostPopularRides(limit);
    }

    // Get favorites count for a user
    public Long getUserFavoritesCount(Long userId) {
        return userFavoriteRepository.countByUserId(userId);
    }

    // Get favorites count for a ride
    public Long getRideFavoritesCount(Long rideId) {
        return userFavoriteRepository.countByRideId(rideId);
    }

    // Get users who favorited a specific ride
    public List<Long> getUsersWhoFavoritedRide(Long rideId) {
        return userFavoriteRepository.findUserIdsByRideId(rideId);
    }

    // Get favorites with notes for a user
    public List<FavoriteDto> getUserFavoritesWithNotes(Long userId) {
        List<UserFavorite> favorites = userFavoriteRepository.findByUserIdWithNotes(userId);
        return favorites.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get favorites statistics
    public Object[] getFavoritesStatistics() {
        return userFavoriteRepository.getFavoritesStatistics();
    }

    // Convert entity to DTO
    private FavoriteDto convertToDto(UserFavorite favorite) {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(favorite.getId());
        dto.setUserId(favorite.getUserId());
        dto.setNotes(favorite.getNotes());
        dto.setAddedAt(favorite.getAddedAt());
        
        // Convert ride to DTO
        RideDto rideDto = rideService.getRideById(favorite.getRide().getId());
        dto.setRide(rideDto);
        
        return dto;
    }
}