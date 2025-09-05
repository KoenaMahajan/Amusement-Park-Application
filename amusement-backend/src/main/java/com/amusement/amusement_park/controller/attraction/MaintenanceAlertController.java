package com.amusement.amusement_park.controller.attraction;

import com.amusement.amusement_park.dto.attraction.MaintenanceAlertCreateDto;
import com.amusement.amusement_park.dto.attraction.MaintenanceAlertDto;
import com.amusement.amusement_park.service.attraction.MaintenanceAlertService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")

public class MaintenanceAlertController {

    @Autowired
    private MaintenanceAlertService maintenanceAlertService;

    // Get all active maintenance alerts (Everyone)
    @GetMapping("/alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<MaintenanceAlertDto>> getAllActiveAlerts() {
        List<MaintenanceAlertDto> alerts = maintenanceAlertService.getAllActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    // Get maintenance alerts for specific ride (Everyone)
    @GetMapping("/alerts/ride/{rideId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<MaintenanceAlertDto>> getAlertsByRideId(@PathVariable Long rideId) {
        List<MaintenanceAlertDto> alerts = maintenanceAlertService.getAlertsByRideId(rideId);
        return ResponseEntity.ok(alerts);
    }

    // Get specific maintenance alert details (Everyone)
    @GetMapping("/alerts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<MaintenanceAlertDto> getAlertById(@PathVariable Long id) {
        MaintenanceAlertDto alert = maintenanceAlertService.getAlertById(id);
        return ResponseEntity.ok(alert);
    }

    // Create new maintenance alert (Admin)
    @PostMapping("/alerts")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MaintenanceAlertDto> createAlert(@Valid @RequestBody MaintenanceAlertCreateDto createDto) {
        MaintenanceAlertDto createdAlert = maintenanceAlertService.createAlert(createDto);
        return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
    }

    // Update maintenance alert (Admin )
    @PutMapping("/alerts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MaintenanceAlertDto> updateAlert(@PathVariable Long id,
            @Valid @RequestBody MaintenanceAlertCreateDto updateDto) {
        MaintenanceAlertDto updatedAlert = maintenanceAlertService.updateAlert(id, updateDto);
        return ResponseEntity.ok(updatedAlert);
    }

    // Cancel/delete maintenance alert (Admin only)
    @DeleteMapping("/alerts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteAlert(@PathVariable Long id) {
        maintenanceAlertService.deleteAlert(id);
        return ResponseEntity.ok(Map.of("message", "Maintenance alert deleted successfully"));
    }

    // Activate/deactivate alert (Admin)
    @PatchMapping("/alerts/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MaintenanceAlertDto> updateAlertStatus(@PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap) {
        Boolean isActive = statusMap.get("isActive");
        if (isActive == null) {
            throw new IllegalArgumentException("isActive field is required");
        }
        MaintenanceAlertDto updatedAlert = maintenanceAlertService.updateAlertStatus(id, isActive);
        return ResponseEntity.ok(updatedAlert);
    }

    // Get upcoming scheduled maintenance (Everyone)
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<MaintenanceAlertDto>> getUpcomingMaintenance() {
        List<MaintenanceAlertDto> alerts = maintenanceAlertService.getUpcomingMaintenance();
        return ResponseEntity.ok(alerts);
    }

    // Get maintenance history for a ride (Admin )
    @GetMapping("/history/{rideId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MaintenanceAlertDto>> getMaintenanceHistory(@PathVariable Long rideId) {
        List<MaintenanceAlertDto> alerts = maintenanceAlertService.getMaintenanceHistory(rideId);
        return ResponseEntity.ok(alerts);
    }

    // Get currently active alerts (Everyone)
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<MaintenanceAlertDto>> getCurrentlyActiveAlerts() {
        List<MaintenanceAlertDto> alerts = maintenanceAlertService.getCurrentlyActiveAlerts();
        return ResponseEntity.ok(alerts);
    }
}
