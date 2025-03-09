package dev.forte.overlandplannerv2.waypoint;

import dev.forte.overlandplannerv2.waypoint.dtos.SimpleWaypointDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaypointRepository extends JpaRepository<WaypointEntity, Long> {

    // Find all waypoints by trip ID
    List<WaypointEntity> findByTripId(Long tripId);

    // Find all waypoints by trip ID and user ID (for ownership validation)
    List<WaypointEntity> findByTripIdAndUserId(Long tripId, Long userId);

    Page<WaypointEntity> findByTripIdAndUserId(Long tripId, Long userID, Pageable pageable);

    // Optionally find a single waypoint by trip and waypoint ID
    WaypointEntity findByIdAndTripId(Long id, Long tripId);


    @Query("SELECT w FROM waypoints w WHERE w.trip.id = :tripId AND w.userId = :userId AND LOWER(w.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<WaypointEntity> findByTripIdAndUserIdAndNameContainingIgnoreCase(
            @Param("tripId") Long tripId,
            @Param("userId") Long userId,
            @Param("query") String query);


    WaypointEntity findByNameAndTripIdAndUserId(String name, Long tripId, Long userId);

    @Query("SELECT new dev.forte.overlandplannerv2.waypoint.dtos.SimpleWaypointDTO(w.id, w.userId,w.trip.id, w.name, w.latitude,w.longitude) " +
            "FROM waypoints w WHERE w.trip.id = :tripId")
    List<SimpleWaypointDTO> findSimpleWaypointsByUserIdAndTripId(@Param("userId") Long userId, @Param("tripId") Long trip_id);
}