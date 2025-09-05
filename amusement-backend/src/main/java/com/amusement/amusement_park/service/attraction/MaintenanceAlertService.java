package com.amusement.amusement_park.service.attraction;


import com.amusement.amusement_park.dto.attraction.MaintenanceAlertCreateDto;
import com.amusement.amusement_park.dto.attraction.MaintenanceAlertDto;
import com.amusement.amusement_park.entity.attraction.MaintenanceAlert;
import com.amusement.amusement_park.entity.attraction.Ride;
import com.amusement.amusement_park.exception.attraction.MaintenanceAlertNotFoundException;
import com.amusement.amusement_park.exception.attraction.MaintenanceConflictException;
import com.amusement.amusement_park.exception.attraction.RideNotFoundException;
import com.amusement.amusement_park.repository.attraction.MaintenanceAlertRepository;
import com.amusement.amusement_park.repository.attraction.RideRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaintenanceAlertService {

    @Autowired
    private MaintenanceAlertRepository maintenanceAlertRepository;

    @Autowired
    private RideRepository rideRepository;

    // Get all active maintenance alerts
    public List<MaintenanceAlertDto> getAllActiveAlerts() {
        List<MaintenanceAlert> alerts = maintenanceAlertRepository.findByIsActiveTrue();
        return alerts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get maintenance alerts for specific ride
    public List<MaintenanceAlertDto> getAlertsByRideId(Long rideId) {
        List<MaintenanceAlert> alerts = maintenanceAlertRepository.findByRideIdOrderByCreatedAtDesc(rideId);
        return alerts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get specific maintenance alert by ID
    public MaintenanceAlertDto getAlertById(Long id) {
        MaintenanceAlert alert = maintenanceAlertRepository.findById(id)
                .orElseThrow(() -> new MaintenanceAlertNotFoundException("Maintenance alert not found with id: " + id));
        return convertToDto(alert);
    }

    // Create new maintenance alert
    public MaintenanceAlertDto createAlert(MaintenanceAlertCreateDto createDto) {
        // Validate ride exists
        Ride ride = rideRepository.findById(createDto.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + createDto.getRideId()));

        // Validate start time
        if (createDto.getStartTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("Start time cannot be in the past");
        }

        // Validate end time
        if (createDto.getEndTime() != null && createDto.getEndTime().isBefore(createDto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping maintenance
        List<MaintenanceAlert> overlapping = maintenanceAlertRepository.findOverlappingMaintenance(
                createDto.getRideId(), createDto.getStartTime(), createDto.getEndTime());
        
        if (!overlapping.isEmpty()) {
            throw new MaintenanceConflictException("Overlapping maintenance already scheduled for this ride during the specified time period");
        }

        MaintenanceAlert alert = new MaintenanceAlert();
        alert.setRide(ride);
        alert.setAlertType(createDto.getAlertType());
        alert.setTitle(createDto.getTitle());
        alert.setDescription(createDto.getDescription());
        alert.setStartTime(createDto.getStartTime());
        alert.setEndTime(createDto.getEndTime());
        alert.setPriority(createDto.getPriority());
        alert.setCreatedBy(createDto.getCreatedBy());
        alert.setIsActive(true);

        MaintenanceAlert savedAlert = maintenanceAlertRepository.save(alert);

        // Update ride operational status if maintenance is currently active
        updateRideOperationalStatus(ride);

        return convertToDto(savedAlert);
    }

    // Update maintenance alert
    public MaintenanceAlertDto updateAlert(Long id, MaintenanceAlertCreateDto updateDto) {
        MaintenanceAlert alert = maintenanceAlertRepository.findById(id)
                .orElseThrow(() -> new MaintenanceAlertNotFoundException("Maintenance alert not found with id: " + id));

        // Validate end time if provided
        if (updateDto.getEndTime() != null && updateDto.getStartTime() != null && 
            updateDto.getEndTime().isBefore(updateDto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping maintenance (excluding current alert)
        if (updateDto.getStartTime() != null) {
            List<MaintenanceAlert> overlapping = maintenanceAlertRepository.findOverlappingMaintenance(
                    alert.getRide().getId(), 
                    updateDto.getStartTime(), 
                    updateDto.getEndTime() != null ? updateDto.getEndTime() : alert.getEndTime());
            
            overlapping = overlapping.stream()
                    .filter(ma -> !ma.getId().equals(id))
                    .collect(Collectors.toList());
            
            if (!overlapping.isEmpty()) {
                throw new MaintenanceConflictException("Overlapping maintenance already scheduled for this ride during the specified time period");
            }
        }

        if (updateDto.getTitle() != null) alert.setTitle(updateDto.getTitle());
        if (updateDto.getDescription() != null) alert.setDescription(updateDto.getDescription());
        if (updateDto.getStartTime() != null) alert.setStartTime(updateDto.getStartTime());
        if (updateDto.getEndTime() != null) alert.setEndTime(updateDto.getEndTime());
        if (updateDto.getPriority() != null) alert.setPriority(updateDto.getPriority());
        if (updateDto.getAlertType() != null) alert.setAlertType(updateDto.getAlertType());

        MaintenanceAlert savedAlert = maintenanceAlertRepository.save(alert);

        // Update ride operational status
        updateRideOperationalStatus(alert.getRide());

        return convertToDto(savedAlert);
    }

    // Delete/Cancel maintenance alert
    public void deleteAlert(Long id) {
        MaintenanceAlert alert = maintenanceAlertRepository.findById(id)
                .orElseThrow(() -> new MaintenanceAlertNotFoundException("Maintenance alert not found with id: " + id));
        
        Ride ride = alert.getRide();
        maintenanceAlertRepository.deleteById(id);
        
        // Update ride operational status after deletion
        updateRideOperationalStatus(ride);
    }

    // Activate/Deactivate alert
    public MaintenanceAlertDto updateAlertStatus(Long id, Boolean isActive) {
        MaintenanceAlert alert = maintenanceAlertRepository.findById(id)
                .orElseThrow(() -> new MaintenanceAlertNotFoundException("Maintenance alert not found with id: " + id));
        
        alert.setIsActive(isActive);
        MaintenanceAlert savedAlert = maintenanceAlertRepository.save(alert);
        
        // Update ride operational status
        updateRideOperationalStatus(alert.getRide());
        
        return convertToDto(savedAlert);
    }

    // Get upcoming scheduled maintenance (next 7 days)
    public List<MaintenanceAlertDto> getUpcomingMaintenance() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(7);
        
        List<MaintenanceAlert> alerts = maintenanceAlertRepository.findUpcomingMaintenance(now, futureDate);
        return alerts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get maintenance history for a ride
    public List<MaintenanceAlertDto> getMaintenanceHistory(Long rideId) {
        List<MaintenanceAlert> alerts = maintenanceAlertRepository.findMaintenanceHistoryByRideId(rideId);
        return alerts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get currently active alerts
    public List<MaintenanceAlertDto> getCurrentlyActiveAlerts() {
        LocalDateTime now = LocalDateTime.now();
        List<MaintenanceAlert> alerts = maintenanceAlertRepository.findCurrentlyActiveAlerts(now);
        return alerts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Update ride operational status based on maintenance alerts
    private void updateRideOperationalStatus(Ride ride) {
        LocalDateTime now = LocalDateTime.now();
        List<MaintenanceAlert> activeAlerts = maintenanceAlertRepository.findByRideIdAndIsActiveTrueOrderByStartTimeAsc(ride.getId());
        
        boolean hasActiveMaintenance = activeAlerts.stream()
                .anyMatch(alert -> alert.getStartTime().isBefore(now.plusMinutes(1)) && 
                                 (alert.getEndTime() == null || alert.getEndTime().isAfter(now)));
        
        ride.setIsOperational(!hasActiveMaintenance);
        rideRepository.save(ride);
    }

    // Process expired alerts (can be called by scheduled task)
    public void processExpiredAlerts() {
        LocalDateTime now = LocalDateTime.now();
        List<MaintenanceAlert> expiredAlerts = maintenanceAlertRepository.findExpiredAlerts(now);
        
        for (MaintenanceAlert alert : expiredAlerts) {
            alert.setIsActive(false);
            maintenanceAlertRepository.save(alert);
            updateRideOperationalStatus(alert.getRide());
        }
    }

    // Convert entity to DTO
    private MaintenanceAlertDto convertToDto(MaintenanceAlert alert) {
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
}