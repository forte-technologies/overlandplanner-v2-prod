package dev.forte.overlandplannerv2.waypoint;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaypointRepository extends JpaRepository<WaypointEntity, Long> {

    // Find all waypoints by trip ID
    List<WaypointEntity> findByTripId(Long tripId);

    // Find all waypoints by trip ID and user ID (for ownership validation)
    List<WaypointEntity> findByTripIdAndUserId(Long tripId, Long userId);

    // Optionally find a single waypoint by trip and waypoint ID
    WaypointEntity findByIdAndTripId(Long id, Long tripId);
}