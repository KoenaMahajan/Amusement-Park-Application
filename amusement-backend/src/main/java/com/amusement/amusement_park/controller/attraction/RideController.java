package com.amusement.amusement_park.controller.attraction;

import com.amusement.amusement_park.Enums.ThrillLevel;
import com.amusement.amusement_park.dto.attraction.RideCreateDto;
import com.amusement.amusement_park.dto.attraction.RideDto;
import com.amusement.amusement_park.dto.attraction.RideUpdateDto;
import com.amusement.amusement_park.service.attraction.RideService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")

public class RideController {

    @Autowired
    private RideService rideService;

    /**
     * Get all rides with pagination, sorting, and filtering
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<RideDto>> getAllRides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) ThrillLevel thrillLevel,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Boolean operational) {

        Page<RideDto> rides = rideService.getAllRides(page, size, sort, direction, thrillLevel, minAge, operational);
        return ResponseEntity.ok(rides);
    }

    /**
     * Get detailed ride information by ID
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RideDto> getRideById(@PathVariable Long id) {
        RideDto ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }

    /**
     * Create new ride
     * Accessible by: ADMIN only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RideDto> createRide(@Valid @RequestBody RideCreateDto createDto) {
        RideDto createdRide = rideService.createRide(createDto);
        return new ResponseEntity<>(createdRide, HttpStatus.CREATED);
    }

    /**
     * Update ride details
     * Accessible by: ADMIN only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RideDto> updateRide(@PathVariable Long id, @Valid @RequestBody RideUpdateDto updateDto) {
        RideDto updatedRide = rideService.updateRide(id, updateDto);
        return ResponseEntity.ok(updatedRide);
    }

    /**
     * Delete ride
     * Accessible by: ADMIN only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.ok(Map.of("message", "Ride deleted successfully"));
    }

    /**
     * Update ride operational status
     * Accessible by: ADMIN only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<RideDto> updateRideStatus(@PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap) {
        Boolean isOperational = statusMap.get("isOperational");
        if (isOperational == null) {
            throw new IllegalArgumentException("isOperational field is required");
        }
        RideDto updatedRide = rideService.updateRideStatus(id, isOperational);
        return ResponseEntity.ok(updatedRide);
    }

    /**
     * Search rides by name, thrill level, age suitability
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Page<RideDto>> searchRides(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ThrillLevel thrillLevel,
            @RequestParam(required = false) Integer suitableForAge,
            @RequestParam(required = false) Boolean operational,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<RideDto> rides = rideService.searchRides(keyword, thrillLevel, suitableForAge, operational, page, size,
                sort, direction);
        return ResponseEntity.ok(rides);
    }

    /**
     * Get rides suitable for specific age
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/suitable-for-age/{age}")
    public ResponseEntity<List<RideDto>> getRidesSuitableForAge(@PathVariable Integer age) {
        List<RideDto> rides = rideService.getRidesSuitableForAge(age);
        return ResponseEntity.ok(rides);
    }

    /**
     * Get available rides
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/available")
    public ResponseEntity<List<RideDto>> getAvailableRides() {
        List<RideDto> rides = rideService.getAvailableRides();
        return ResponseEntity.ok(rides);
    }

    /**
     * Get rides by thrill level
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/thrill-level/{thrillLevel}")
    public ResponseEntity<List<RideDto>> getRidesByThrillLevel(@PathVariable ThrillLevel thrillLevel) {
        List<RideDto> rides = rideService.getRidesByThrillLevel(thrillLevel);
        return ResponseEntity.ok(rides);
    }

    /**
     * Get most popular rides
     * Accessible by: USER, ADMIN
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/popular")
    public ResponseEntity<List<RideDto>> getMostPopularRides(@RequestParam(defaultValue = "10") int limit) {
        List<RideDto> rides = rideService.getMostPopularRides(limit);
        return ResponseEntity.ok(rides);
    }
}
