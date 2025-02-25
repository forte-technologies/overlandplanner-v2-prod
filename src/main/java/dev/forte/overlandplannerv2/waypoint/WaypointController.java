package dev.forte.overlandplannerv2.waypoint;

import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.UpdateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.WaypointDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;

@Slf4j
@RestController
@RequestMapping("/api/user/trips/{tripId}/waypoints")
@PreAuthorize("isAuthenticated()")
public class WaypointController {

    private final WaypointService waypointService;

    public WaypointController(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @PostMapping
    public WaypointDTO addWaypoint(Authentication authentication,
                                            @PathVariable Long tripId,
                                            @RequestBody CreateWaypointDTO waypointDTO) {
        Long userId = getAuthenticatedUserId(authentication);
        return waypointService.addWaypoint(waypointDTO, userId, tripId);

    }

    @GetMapping
    public ResponseEntity<List<WaypointDTO>> getWaypoints(Authentication authentication,
                                                          @PathVariable Long tripId) {
        Long userId = getAuthenticatedUserId(authentication);
        List<WaypointDTO> waypoints = waypointService.getWaypoints(userId, tripId);
        return ResponseEntity.ok(waypoints);
    }

    @GetMapping("/{waypointId}")
    public ResponseEntity<WaypointDTO> getWaypoint(Authentication authentication,
                                                   @PathVariable Long tripId,
                                                   @PathVariable Long waypointId) {
        Long userId = getAuthenticatedUserId(authentication);
        WaypointDTO waypoint = waypointService.getWayPointByTripId(userId, tripId, waypointId);
        return ResponseEntity.ok(waypoint);
    }

    @PutMapping("/{waypointId}")
    public ResponseEntity<?> updateWaypoint(Authentication authentication,
                                            @PathVariable Long tripId,
                                            @PathVariable Long waypointId,
                                            @RequestBody UpdateWaypointDTO waypointDTO) {
        try {
            Long userId = getAuthenticatedUserId(authentication);
            WaypointDTO updatedWaypoint = waypointService.updateWaypoint(userId, tripId, waypointId, waypointDTO);
            return ResponseEntity.ok(updatedWaypoint);
        } catch (Exception e) {
            log.error("Error updating waypoint: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating waypoint: " + e.getMessage());
        }
    }


    @DeleteMapping("/{waypointId}")
    public ResponseEntity<Void> deleteWaypoint(Authentication authentication,
                                               @PathVariable Long tripId,
                                               @PathVariable Long waypointId) {
        Long userId = getAuthenticatedUserId(authentication);
        waypointService.deleteWaypoint(userId, tripId, waypointId);
        return ResponseEntity.noContent().build();
    }
}