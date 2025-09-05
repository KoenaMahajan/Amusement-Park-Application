package com.amusement.amusement_park.repository.attraction;


import com.amusement.amusement_park.Enums.AlertType;
import com.amusement.amusement_park.Enums.Priority;
import com.amusement.amusement_park.entity.attraction.MaintenanceAlert;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceAlertRepository extends JpaRepository<MaintenanceAlert, Long> {

    // Find all active alerts
    List<MaintenanceAlert> findByIsActiveTrue();

    // Find alerts by ride ID
    List<MaintenanceAlert> findByRideIdOrderByCreatedAtDesc(Long rideId);

    // Find active alerts by ride ID
    List<MaintenanceAlert> findByRideIdAndIsActiveTrueOrderByStartTimeAsc(Long rideId);

    // Find alerts by type
    List<MaintenanceAlert> findByAlertType(AlertType alertType);

    // Find alerts by priority
    List<MaintenanceAlert> findByPriority(Priority priority);

    // Find currently active alerts (started but not ended)
    @Query("SELECT ma FROM MaintenanceAlert ma WHERE ma.isActive = true AND ma.startTime <= :now AND (ma.endTime IS NULL OR ma.endTime > :now)")
    List<MaintenanceAlert> findCurrentlyActiveAlerts(@Param("now") LocalDateTime now);

    // Find upcoming scheduled maintenance (next 7 days)
    @Query("SELECT ma FROM MaintenanceAlert ma WHERE ma.isActive = true AND ma.startTime > :now AND ma.startTime <= :futureDate ORDER BY ma.startTime ASC")
    List<MaintenanceAlert> findUpcomingMaintenance(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    // Find overlapping maintenance for a ride
    @Query("SELECT ma FROM MaintenanceAlert ma WHERE ma.ride.id = :rideId AND ma.isActive = true AND " +
           "((ma.startTime <= :endTime AND (ma.endTime IS NULL OR ma.endTime >= :startTime)) OR " +
           "(ma.endTime IS NULL AND ma.startTime <= :endTime))")
    List<MaintenanceAlert> findOverlappingMaintenance(@Param("rideId") Long rideId, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);

    // Find maintenance history for a ride
    @Query("SELECT ma FROM MaintenanceAlert ma WHERE ma.ride.id = :rideId ORDER BY ma.createdAt DESC")
    List<MaintenanceAlert> findMaintenanceHistoryByRideId(@Param("rideId") Long rideId);

    // Find alerts by created by
    List<MaintenanceAlert> findByCreatedByOrderByCreatedAtDesc(String createdBy);

    // Count active alerts by priority
    Long countByIsActiveTrueAndPriority(Priority priority);

    // Find alerts ending soon (within specified hours)
    @Query("SELECT ma FROM MaintenanceAlert ma WHERE ma.isActive = true AND ma.endTime IS NOT NULL AND ma.endTime > :now AND ma.endTime <= :futureTime")
    List<MaintenanceAlert> findAlertsEndingSoon(@Param("now") LocalDateTime now, @Param("futureTime") LocalDateTime futureTime);

    // Find expired alerts that should be deactivated
    @Query("SELECT ma FROM MaintenanceAlert ma WHERE ma.isActive = true AND ma.endTime IS NOT NULL AND ma.endTime < :now")
    List<MaintenanceAlert> findExpiredAlerts(@Param("now") LocalDateTime now);

    // Check for maintenance conflicts before creating new alert
    @Query("SELECT COUNT(ma) > 0 FROM MaintenanceAlert ma WHERE ma.ride.id = :rideId AND ma.isActive = true AND " +
           "ma.alertType = :alertType AND " +
           "((ma.startTime <= :endTime AND (ma.endTime IS NULL OR ma.endTime >= :startTime)) OR " +
           "(ma.endTime IS NULL AND ma.startTime <= :endTime)) AND " +
           "(:excludeId IS NULL OR ma.id != :excludeId)")
    Boolean hasMaintenanceConflict(@Param("rideId") Long rideId, 
                                  @Param("alertType") AlertType alertType,
                                  @Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("excludeId") Long excludeId);
}