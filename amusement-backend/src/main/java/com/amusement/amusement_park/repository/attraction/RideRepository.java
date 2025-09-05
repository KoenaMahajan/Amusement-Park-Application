package com.amusement.amusement_park.repository.attraction;


import com.amusement.amusement_park.Enums.ThrillLevel;
import com.amusement.amusement_park.entity.attraction.Ride;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find rides by name (case insensitive)
    List<Ride> findByNameContainingIgnoreCase(String name);

    // Find rides by thrill level
    List<Ride> findByThrillLevel(ThrillLevel thrillLevel);

    // Find operational rides
    List<Ride> findByIsOperational(Boolean isOperational);

    // Find rides suitable for a specific age
    @Query("SELECT r FROM Ride r WHERE r.minAge <= :age AND (r.maxAge IS NULL OR r.maxAge >= :age)")
    List<Ride> findRidesSuitableForAge(@Param("age") Integer age);

    // Search rides with multiple criteria
    @Query("SELECT r FROM Ride r WHERE " +
           "(:keyword IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:thrillLevel IS NULL OR r.thrillLevel = :thrillLevel) AND " +
           "(:minAge IS NULL OR (r.minAge <= :minAge AND (r.maxAge IS NULL OR r.maxAge >= :minAge))) AND " +
           "(:isOperational IS NULL OR r.isOperational = :isOperational)")
    Page<Ride> searchRides(@Param("keyword") String keyword,
                          @Param("thrillLevel") ThrillLevel thrillLevel,
                          @Param("minAge") Integer minAge,
                          @Param("isOperational") Boolean isOperational,
                          Pageable pageable);

    // Find rides with height requirements
    @Query("SELECT r FROM Ride r WHERE r.heightRequirementCm IS NOT NULL AND r.heightRequirementCm <= :height")
    List<Ride> findRidesSuitableForHeight(@Param("height") Integer height);

    @Query("SELECT r FROM Ride r WHERE " +
       "(:thrillLevel IS NULL OR r.thrillLevel = :thrillLevel) AND " +
       "(:minAge IS NULL OR r.minAge <= :minAge) AND " +
       "(:isOperational IS NULL OR r.isOperational = :isOperational)")
       Page<Ride> findAllWithFilters(@Param("thrillLevel") ThrillLevel thrillLevel,
                               @Param("minAge") Integer minAge,
                               @Param("isOperational") Boolean isOperational,
                               Pageable pageable);


    // Find rides by location description
    List<Ride> findByLocationDescriptionContainingIgnoreCase(String location);

    // Find rides by duration range
    @Query("SELECT r FROM Ride r WHERE r.durationMinutes BETWEEN :minDuration AND :maxDuration")
    List<Ride> findByDurationRange(@Param("minDuration") Integer minDuration, 
                                  @Param("maxDuration") Integer maxDuration);

    // Count rides by thrill level
    Long countByThrillLevel(ThrillLevel thrillLevel);

    // Check if ride name exists (excluding specific ID for updates)
    @Query("SELECT COUNT(r) > 0 FROM Ride r WHERE LOWER(r.name) = LOWER(:name) AND (:excludeId IS NULL OR r.id != :excludeId)")
    Boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("excludeId") Long excludeId);

    // Find rides with active maintenance
    @Query("SELECT DISTINCT r FROM Ride r JOIN r.maintenanceAlerts ma WHERE ma.isActive = true AND ma.startTime <= CURRENT_TIMESTAMP AND (ma.endTime IS NULL OR ma.endTime > CURRENT_TIMESTAMP)")
    List<Ride> findRidesWithActiveMaintenance();

    // Find available rides (operational and no active maintenance)
    @Query("SELECT r FROM Ride r WHERE r.isOperational = true AND r.id NOT IN " +
           "(SELECT DISTINCT ma.ride.id FROM MaintenanceAlert ma WHERE ma.isActive = true AND ma.startTime <= CURRENT_TIMESTAMP AND (ma.endTime IS NULL OR ma.endTime > CURRENT_TIMESTAMP))")
    List<Ride> findAvailableRides();

    // Get most popular rides (by favorites count)
    @Query("SELECT r, COUNT(uf) as favCount FROM Ride r LEFT JOIN r.userFavorites uf GROUP BY r ORDER BY favCount DESC")
    List<Object[]> findMostPopularRides(Pageable pageable);
}