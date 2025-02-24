package dev.forte.overlandplannerv2.waypoint;

import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.UpdateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.WaypointDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;

@RestController
@RequestMapping("/api/user/trips/{tripId}/waypoints")
@PreAuthorize("isAuthenticated()")
public class WaypointController {

    private final WaypointService waypointService;

    public WaypointController(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @PostMapping
    public ResponseEntity<Void> addWaypoint(Authentication authentication,
                                            @PathVariable Long tripId,
                                            @RequestBody CreateWaypointDTO waypointDTO) {
        Long userId = getAuthenticatedUserId(authentication);
        waypointService.addWaypoint(waypointDTO, userId, tripId);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<WaypointDTO> updateWaypoint(Authentication authentication,
                                                      @PathVariable Long tripId,
                                                      @PathVariable Long waypointId,
                                                      @RequestBody UpdateWaypointDTO waypointDTO) {
        Long userId = getAuthenticatedUserId(authentication);
        WaypointDTO updatedWaypoint = waypointService.updateWaypoint(userId, tripId, waypointId, waypointDTO);
        return ResponseEntity.ok(updatedWaypoint);
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