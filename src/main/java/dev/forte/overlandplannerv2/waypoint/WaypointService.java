package dev.forte.overlandplannerv2.waypoint;

import dev.forte.overlandplannerv2.trip.TripEntity;
import dev.forte.overlandplannerv2.trip.TripRepository;
import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.UpdateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.WaypointDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WaypointService {

    private final WaypointRepository waypointRepository;
    private final TripRepository tripRepository;

    public WaypointService(WaypointRepository waypointRepository, TripRepository tripRepository) {
        this.waypointRepository = waypointRepository;
        this.tripRepository = tripRepository;
    }

    public void addWaypoint(CreateWaypointDTO createWaypointDTO, Long userId, Long tripId) {
        TripEntity tripEntity = tripRepository.findByIdAndUserId(tripId, userId);
        if (tripEntity == null) {
            throw new RuntimeException("Trip not found or access denied.");
        }
        WaypointEntity waypointEntity = new WaypointEntity();
        waypointEntity.setTrip(tripEntity);
        waypointEntity.setUserId(userId);
        waypointEntity.setName(createWaypointDTO.getName());
        waypointEntity.setDescription(createWaypointDTO.getDescription());
        waypointEntity.setLatitude(Double.valueOf(createWaypointDTO.getLatitude()));
        waypointEntity.setLongitude(Double.valueOf(createWaypointDTO.getLongitude()));

        waypointRepository.save(waypointEntity);
    }

    public List<WaypointDTO> getWaypoints(Long userId, Long tripId){
        List<WaypointEntity> waypointEntities = waypointRepository.findByTripIdAndUserId(tripId, userId);
        if (waypointEntities.isEmpty()){
            log.info("No waypoints found for user: {}", userId);
        } else {
           log.info("Found {} waypoints for user: {}", waypointEntities.size(), userId);
        }
        return waypointEntities.stream().map(WaypointDTO::new).toList();
    }

    public WaypointDTO getWayPointByTripId(Long userId, Long tripId, Long waypointID){
        WaypointEntity waypointEntity = waypointRepository.findById(waypointID)
                .orElseThrow(() -> new RuntimeException("Waypoint not found."));

        if (!waypointEntity.getTrip().getId().equals(tripId)
                || !waypointEntity.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied or wrong trip ID.");
        }
        return new WaypointDTO(waypointEntity);
    }

    public void deleteWaypoint(Long userId, Long tripId, Long waypointId){
        WaypointEntity waypointEntity = waypointRepository.findByIdAndTripId(waypointId,tripId);
        if (waypointEntity == null){
            throw new RuntimeException("Waypoint not found.");
        }
        if (!waypointEntity.getUserId().equals(userId)){
            throw new RuntimeException("Access denied.");
        }
        waypointRepository.delete(waypointEntity);
    }

    public WaypointDTO updateWaypoint(Long userId, Long tripId, Long waypointId, UpdateWaypointDTO updateWaypointDTO){
        WaypointEntity waypointEntity = waypointRepository.findByIdAndTripId(waypointId,tripId);
        if (waypointEntity == null){
            throw new RuntimeException("Waypoint not found.");
        }
        if (!waypointEntity.getUserId().equals(userId)){
            throw new RuntimeException("Access denied.");
        }
        if (updateWaypointDTO.getName() != null){
            waypointEntity.setName(updateWaypointDTO.getName());
        }
        if (updateWaypointDTO.getDescription() != null){
            waypointEntity.setDescription(updateWaypointDTO.getDescription());
        }
        if (updateWaypointDTO.getLatitude() != null){
            waypointEntity.setLatitude(updateWaypointDTO.getLatitude());
        }
        if (updateWaypointDTO.getLongitude() != null){
            waypointEntity.setLongitude(updateWaypointDTO.getLongitude());
        }
        waypointRepository.save(waypointEntity);
        return new WaypointDTO(waypointEntity);
    }
}
