package com.amusement.amusement_park.repository.attraction;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.amusement.amusement_park.entity.attraction.RidePhoto;

import java.util.List;
import java.util.Optional;

@Repository
public interface RidePhotoRepository extends JpaRepository<RidePhoto, Long> {

    // Find all photos for a ride
    List<RidePhoto> findByRideIdOrderByUploadedAtDesc(Long rideId);

    // Find primary photo for a ride
    Optional<RidePhoto> findByRideIdAndIsPrimaryTrue(Long rideId);

    // Find photos with captions
    @Query("SELECT rp FROM RidePhoto rp WHERE rp.ride.id = :rideId AND rp.caption IS NOT NULL AND LENGTH(rp.caption) > 0")
    List<RidePhoto> findByRideIdWithCaptions(@Param("rideId") Long rideId);

    // Count photos for a ride
    Long countByRideId(Long rideId);

    // Check if ride has primary photo
    Boolean existsByRideIdAndIsPrimaryTrue(Long rideId);
}