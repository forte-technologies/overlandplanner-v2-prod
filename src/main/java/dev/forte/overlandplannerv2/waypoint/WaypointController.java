package dev.forte.overlandplannerv2.waypoint;

import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.UpdateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.WaypointDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedWaypoints(Authentication authentication, @PathVariable Long tripId,
                                                                     @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        Long userId = getAuthenticatedUserId(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<WaypointDTO> waypointDTOPage = waypointService.getPaginatedWaypoints(userId, tripId, pageable);

        Map<String,Object> response = new HashMap<>();
        response.put("waypoints", waypointDTOPage.getContent());
        response.put("currentPage", waypointDTOPage.getNumber());
        response.put("totalItems", waypointDTOPage.getTotalElements());
        response.put("totalPages", waypointDTOPage.getTotalPages());

        return ResponseEntity.ok(response);
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


    @GetMapping("/search")
    public ResponseEntity<List<WaypointDTO>> searchWaypoints(
            Authentication authentication,
            @RequestParam String query,
            @PathVariable Long tripId,
            @RequestParam(defaultValue = "false") boolean exactMatch) {

            Long userId = getAuthenticatedUserId(authentication);
            List<WaypointDTO> waypoints = waypointService.searchWaypointsByName(userId, tripId, query, exactMatch);
            return ResponseEntity.ok(waypoints);

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