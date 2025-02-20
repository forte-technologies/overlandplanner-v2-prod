package dev.forte.overlandplannerv2.trip;

import dev.forte.overlandplannerv2.trip.dtos.CreateTripDTO;
import dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO;
import dev.forte.overlandplannerv2.trip.dtos.TripDTO;
import dev.forte.overlandplannerv2.trip.dtos.UpdateTripDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}









