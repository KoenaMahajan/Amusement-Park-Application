package com.amusement.amusement_park.repository.attraction;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.amusement.amusement_park.entity.attraction.UserFavorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    // Find all favorites for a user
    List<UserFavorite> findByUserIdOrderByAddedAtDesc(Long userId);

    // Find favorite by user and ride
    Optional<UserFavorite> findByUserIdAndRideId(Long userId, Long rideId);

    // Check if user has favorited a ride
    Boolean existsByUserIdAndRideId(Long userId, Long rideId);

    // Count favorites for a user
    Long countByUserId(Long userId);

    // Count favorites for a ride
    Long countByRideId(Long rideId);

    // Find users who favorited a specific ride
    @Query("SELECT uf.userId FROM UserFavorite uf WHERE uf.ride.id = :rideId")
    List<Long> findUserIdsByRideId(@Param("rideId") Long rideId);

    // Find most favorited rides
    @Query("SELECT uf.ride.id, COUNT(uf) as favCount FROM UserFavorite uf GROUP BY uf.ride.id ORDER BY favCount DESC")
    List<Object[]> findMostFavoritedRides();

    // Find favorites by ride thrill level for a user
    @Query("SELECT uf FROM UserFavorite uf WHERE uf.userId = :userId AND uf.ride.thrillLevel = :thrillLevel")
    List<UserFavorite> findByUserIdAndRideThrillLevel(@Param("userId") Long userId, @Param("thrillLevel") String thrillLevel);

    // Find favorites with notes for a user
    @Query("SELECT uf FROM UserFavorite uf WHERE uf.userId = :userId AND uf.notes IS NOT NULL AND LENGTH(uf.notes) > 0")
    List<UserFavorite> findByUserIdWithNotes(@Param("userId") Long userId);

    // Delete favorite by user and ride
    void deleteByUserIdAndRideId(Long userId, Long rideId);

    // Find common favorites between users
    @Query("SELECT uf1.ride FROM UserFavorite uf1 JOIN UserFavorite uf2 ON uf1.ride.id = uf2.ride.id WHERE uf1.userId = :userId1 AND uf2.userId = :userId2")
    List<Object[]> findCommonFavorites(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Get favorites statistics
    @Query("SELECT " +
           "COUNT(DISTINCT uf.userId) as totalUsers, " +
           "COUNT(uf) as totalFavorites, " +
           "AVG(subquery.favCount) as avgFavoritesPerUser " +
           "FROM UserFavorite uf, " +
           "(SELECT COUNT(uf2) as favCount FROM UserFavorite uf2 GROUP BY uf2.userId) as subquery")
    Object[] getFavoritesStatistics();
}