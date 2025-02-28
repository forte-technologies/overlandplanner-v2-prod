package dev.forte.overlandplannerv2.trip;

import dev.forte.overlandplannerv2.trip.dtos.CreateTripDTO;
import dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO;
import dev.forte.overlandplannerv2.trip.dtos.TripDTO;
import dev.forte.overlandplannerv2.trip.dtos.UpdateTripDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;

@Slf4j
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }


    @PostMapping
    public ResponseEntity<List<TripDTO>> createTrip(@RequestBody CreateTripDTO createTripDTO,
                                                    Authentication authentication){
        Long userId = getAuthenticatedUserId(authentication);
        tripService.addTrip(createTripDTO, userId);
        List<TripDTO> tripDTOS = tripService.getTrips(userId);

        return ResponseEntity.ok(tripDTOS);
    }


    @GetMapping
    public ResponseEntity<List<TripDTO>> getUserTrips(Authentication authentication){
        log.info("Trips request received");
        Long userId = getAuthenticatedUserId(authentication);
        log.info("Authenticated User ID: {}", userId);

        List<TripDTO> tripDTOS = tripService.getTrips(userId);
        tripDTOS.forEach(trip -> log.info("Trip fetched: {}", trip));

        return ResponseEntity.ok(tripDTOS);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedUserTrips(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        
        log.info("Paginated trips request received for page: {}, size: {}", page, size);
        Long userId = getAuthenticatedUserId(authentication);
        log.info("Authenticated User ID: {}", userId);
        
        // Create pageable object with sorting by id in descending order (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        Page<TripDTO> tripPage = tripService.getPaginatedTrips(userId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("trips", tripPage.getContent());
        response.put("currentPage", tripPage.getNumber());
        response.put("totalItems", tripPage.getTotalElements());
        response.put("totalPages", tripPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripDTO> getUserTrip(Authentication authentication,
                                               @PathVariable Long tripId){

        log.info("Trip request received");
        Long userId = getAuthenticatedUserId(authentication);
        TripDTO tripDTO = tripService.getTripByUser(userId,tripId);
        return ResponseEntity.ok(tripDTO);
    }

    @PutMapping("/{tripID}")
    public ResponseEntity<TripDTO> updateTrip(Authentication authentication,
                                              @PathVariable Long tripID,
                                              @RequestBody UpdateTripDTO updateTripDTO) {

        Long userId = getAuthenticatedUserId(authentication);
        log.info("Update data received: {}", updateTripDTO);

        TripDTO updatedTrip = tripService.updateTripByUser(userId, tripID, updateTripDTO);
        return ResponseEntity.ok(updatedTrip);

    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteUserTrip(
            @PathVariable Long tripId, Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);
        log.info("Trip delete request received for trip ID: {} by user ID: {}", tripId, userId);
        tripService.deleteTripByUser(userId,tripId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/simple")
    public ResponseEntity<List<SimpleTripDTO>> getUserSimpleTrips(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        List<SimpleTripDTO> simpleTrips = tripService.getSimpleTripsByUser(userId);
        return ResponseEntity.ok(simpleTrips);
    }

    @GetMapping("/latest")
    public ResponseEntity<TripDTO> getLatestTrip(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        TripDTO latestTrip = tripService.getLatestTrip(userId);
        return ResponseEntity.ok(latestTrip);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripDTO>> searchTrips(
            Authentication authentication,
            @RequestParam String query,
            @RequestParam(defaultValue = "false") boolean exactMatch) {
        Long userId = getAuthenticatedUserId(authentication);
        List<TripDTO> trips = tripService.searchTripsByName(userId, query, exactMatch);
        return ResponseEntity.ok(trips);
    }

}









