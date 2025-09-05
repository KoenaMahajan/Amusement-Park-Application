package com.amusement.amusement_park.service.attraction;


import com.amusement.amusement_park.Enums.ThrillLevel;
import com.amusement.amusement_park.dto.attraction.MaintenanceAlertDto;
import com.amusement.amusement_park.dto.attraction.RideCreateDto;
import com.amusement.amusement_park.dto.attraction.RideDto;
import com.amusement.amusement_park.dto.attraction.RidePhotoDto;
import com.amusement.amusement_park.dto.attraction.RideUpdateDto;
import com.amusement.amusement_park.entity.attraction.MaintenanceAlert;
import com.amusement.amusement_park.entity.attraction.Ride;
import com.amusement.amusement_park.entity.attraction.RidePhoto;

import com.amusement.amusement_park.exception.attraction.InvalidAgeRestrictionException;
import com.amusement.amusement_park.exception.attraction.RideNotFoundException;
import com.amusement.amusement_park.repository.attraction.RideRepository;
import com.amusement.amusement_park.repository.attraction.UserFavoriteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    
    // Get all rides with optional filtering
public Page<RideDto> getAllRides(int page, int size, String sortBy, String sortDir,
                                 ThrillLevel thrillLevel, Integer minAge, Boolean isOperational) {
    Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() :
            Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Ride> rides = rideRepository.findAllWithFilters(thrillLevel, minAge, isOperational, pageable);

    return rides.map(this::convertToDto);
}


    // Get ride by ID
    public RideDto getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
        return convertToDto(ride);
    }

    // Create new ride
    public RideDto createRide(RideCreateDto createDto) {
        validateAgeRestrictions(createDto.getMinAge(), createDto.getMaxAge());
        
        // Check for duplicate names
        if (rideRepository.existsByNameIgnoreCaseAndIdNot(createDto.getName(), null)) {
            throw new IllegalArgumentException("A ride with this name already exists");
        }

        Ride ride = new Ride();
        ride.setName(createDto.getName());
        ride.setDescription(createDto.getDescription());
        ride.setThrillLevel(createDto.getThrillLevel());
        ride.setMinAge(createDto.getMinAge());
        ride.setMaxAge(createDto.getMaxAge());
        ride.setDurationMinutes(createDto.getDurationMinutes());
        ride.setHeightRequirementCm(createDto.getHeightRequirementCm());
        ride.setPhotoUrl(createDto.getPhotoUrl());
        ride.setVideoUrl(createDto.getVideoUrl());
        ride.setLocationDescription(createDto.getLocationDescription());
        ride.setSafetyInstructions(createDto.getSafetyInstructions());
        ride.setIsOperational(true);

        Ride savedRide = rideRepository.save(ride);
        return convertToDto(savedRide);
    }

    // Update ride
    public RideDto updateRide(Long id, RideUpdateDto updateDto) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));

        if (updateDto.getName() != null) {
            if (rideRepository.existsByNameIgnoreCaseAndIdNot(updateDto.getName(), id)) {
                throw new IllegalArgumentException("A ride with this name already exists");
            }
            ride.setName(updateDto.getName());
        }

        if (updateDto.getMinAge() != null || updateDto.getMaxAge() != null) {
            Integer minAge = updateDto.getMinAge() != null ? updateDto.getMinAge() : ride.getMinAge();
            Integer maxAge = updateDto.getMaxAge() != null ? updateDto.getMaxAge() : ride.getMaxAge();
            validateAgeRestrictions(minAge, maxAge);
            
            if (updateDto.getMinAge() != null) ride.setMinAge(updateDto.getMinAge());
            if (updateDto.getMaxAge() != null) ride.setMaxAge(updateDto.getMaxAge());
        }

        if (updateDto.getDescription() != null) ride.setDescription(updateDto.getDescription());
        if (updateDto.getThrillLevel() != null) ride.setThrillLevel(updateDto.getThrillLevel());
        if (updateDto.getDurationMinutes() != null) ride.setDurationMinutes(updateDto.getDurationMinutes());
        if (updateDto.getHeightRequirementCm() != null) ride.setHeightRequirementCm(updateDto.getHeightRequirementCm());
        if (updateDto.getPhotoUrl() != null) ride.setPhotoUrl(updateDto.getPhotoUrl());
        if (updateDto.getVideoUrl() != null) ride.setVideoUrl(updateDto.getVideoUrl());
        if (updateDto.getLocationDescription() != null) ride.setLocationDescription(updateDto.getLocationDescription());
        if (updateDto.getSafetyInstructions() != null) ride.setSafetyInstructions(updateDto.getSafetyInstructions());

        Ride savedRide = rideRepository.save(ride);
        return convertToDto(savedRide);
    }

    // Delete ride
    public void deleteRide(Long id) {
        if (!rideRepository.existsById(id)) {
            throw new RideNotFoundException("Ride not found with id: " + id);
        }
        rideRepository.deleteById(id);
    }

    // Update ride operational status
    public RideDto updateRideStatus(Long id, Boolean isOperational) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
        
        ride.setIsOperational(isOperational);
        Ride savedRide = rideRepository.save(ride);
        return convertToDto(savedRide);
    }

    // Search rides
    public Page<RideDto> searchRides(String keyword, ThrillLevel thrillLevel, Integer suitableForAge, 
                                   Boolean isOperational, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Ride> rides = rideRepository.searchRides(keyword, thrillLevel, suitableForAge, isOperational, pageable);
        return rides.map(this::convertToDto);
    }

    // Get rides suitable for age
    public List<RideDto> getRidesSuitableForAge(Integer age) {
        List<Ride> rides = rideRepository.findRidesSuitableForAge(age);
        return rides.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get available rides (operational and no active maintenance)
    public List<RideDto> getAvailableRides() {
        List<Ride> rides = rideRepository.findAvailableRides();
        return rides.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get rides by thrill level
    public List<RideDto> getRidesByThrillLevel(ThrillLevel thrillLevel) {
        List<Ride> rides = rideRepository.findByThrillLevel(thrillLevel);
        return rides.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get most popular rides
    public List<RideDto> getMostPopularRides(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = rideRepository.findMostPopularRides(pageable);
        
        return results.stream()
                .map(result -> {
                    Ride ride = (Ride) result[0];
                    return convertToDto(ride);
                })
                .collect(Collectors.toList());
    }

    // Validate age restrictions
    private void validateAgeRestrictions(Integer minAge, Integer maxAge) {
        if (minAge != null && maxAge != null && minAge > maxAge) {
            throw new InvalidAgeRestrictionException("Minimum age cannot be greater than maximum age");
        }
    }

    // Convert entity to DTO
    private RideDto convertToDto(Ride ride) {
        RideDto dto = new RideDto();
        dto.setId(ride.getId());
        dto.setName(ride.getName());
        dto.setDescription(ride.getDescription());
        dto.setThrillLevel(ride.getThrillLevel());
        dto.setMinAge(ride.getMinAge());
        dto.setMaxAge(ride.getMaxAge());
        dto.setDurationMinutes(ride.getDurationMinutes());
        dto.setHeightRequirementCm(ride.getHeightRequirementCm());
        dto.setPhotoUrl(ride.getPhotoUrl());
        dto.setVideoUrl(ride.getVideoUrl());
        dto.setIsOperational(ride.getIsOperational());
        dto.setIsAvailable(ride.isAvailable());
        dto.setLocationDescription(ride.getLocationDescription());
        dto.setSafetyInstructions(ride.getSafetyInstructions());
        dto.setCreatedAt(ride.getCreatedAt());
        dto.setUpdatedAt(ride.getUpdatedAt());

        // Set active maintenance alerts
        List<MaintenanceAlertDto> activeAlerts = ride.getMaintenanceAlerts().stream()
                .filter(alert -> alert.isCurrentlyActive())
                .map(this::convertMaintenanceAlertToDto)
                .collect(Collectors.toList());
        dto.setActiveMaintenanceAlerts(activeAlerts);

        // Set favorites count
        Long favoritesCount = userFavoriteRepository.countByRideId(ride.getId());
        dto.setFavoritesCount(favoritesCount.intValue());

        // Set photos
        List<RidePhotoDto> photos = ride.getRidePhotos().stream()
                .map(this::convertRidePhotoToDto)
                .collect(Collectors.toList());
        dto.setPhotos(photos);

        return dto;
    }

    // Convert MaintenanceAlert to DTO (simplified)
    private MaintenanceAlertDto convertMaintenanceAlertToDto(MaintenanceAlert alert) {
        MaintenanceAlertDto dto = new MaintenanceAlertDto();
        dto.setId(alert.getId());
        dto.setRideId(alert.getRide().getId());
        dto.setRideName(alert.getRide().getName());
        dto.setAlertType(alert.getAlertType());
        dto.setTitle(alert.getTitle());
        dto.setDescription(alert.getDescription());
        dto.setStartTime(alert.getStartTime());
        dto.setEndTime(alert.getEndTime());
        dto.setIsActive(alert.getIsActive());
        dto.setIsCurrentlyActive(alert.isCurrentlyActive());
        dto.setPriority(alert.getPriority());
        dto.setCreatedBy(alert.getCreatedBy());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setUpdatedAt(alert.getUpdatedAt());
        return dto;
    }

    // Convert RidePhoto to DTO
    private RidePhotoDto convertRidePhotoToDto(RidePhoto photo) {
        RidePhotoDto dto = new RidePhotoDto();
        dto.setId(photo.getId());
        dto.setPhotoUrl(photo.getPhotoUrl());
        dto.setCaption(photo.getCaption());
        dto.setIsPrimary(photo.getIsPrimary());
        dto.setUploadedAt(photo.getUploadedAt());
        return dto;
    }
}