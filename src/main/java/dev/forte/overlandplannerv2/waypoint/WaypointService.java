package dev.forte.overlandplannerv2.waypoint;

import dev.forte.overlandplannerv2.trip.TripEntity;
import dev.forte.overlandplannerv2.trip.TripRepository;
import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.UpdateWaypointDTO;
import dev.forte.overlandplannerv2.waypoint.dtos.WaypointDTO;
import dev.forte.overlandplannerv2.weather.WeatherDTO;
import dev.forte.overlandplannerv2.weather.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class WaypointService {

    private final WaypointRepository waypointRepository;
    private final TripRepository tripRepository;
    private final WeatherService weatherService;

    public WaypointService(WaypointRepository waypointRepository, TripRepository tripRepository, WeatherService weatherService) {
        this.waypointRepository = waypointRepository;
        this.tripRepository = tripRepository;
        this.weatherService = weatherService;
    }

    @Transactional
    public WaypointDTO addWaypoint(CreateWaypointDTO createWaypointDTO, Long userId, Long tripId) {
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
        waypointEntity.setStartDate(createWaypointDTO.getStartDate());
        waypointEntity.setEndDate(createWaypointDTO.getEndDate());

        waypointRepository.save(waypointEntity);
        WaypointDTO waypoint = new WaypointDTO(waypointEntity);
        
        // If start and end dates are both set, fetch and update weather data
        if (waypointEntity.getStartDate() != null && waypointEntity.getEndDate() != null) {
            try {
                log.info("Dates set for new waypoint {}. Fetching weather data.", waypointEntity.getId());
                WeatherDTO weatherDTO = weatherService.getWeatherForWaypoint(userId, tripId, waypointEntity.getId());
                waypoint.setWeather(weatherDTO);
                log.info("Weather data fetched successfully for waypoint {}", waypointEntity.getId());
            } catch (Exception e) {
                log.error("Failed to fetch weather data for waypoint {}: {}", waypointEntity.getId(), e.getMessage());
                // We don't want to fail the entire waypoint creation if weather fetch fails
                // Just log the error and continue
            }
        }
        
        return waypoint;
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

    @Transactional
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

    @Transactional
    public WaypointDTO updateWaypoint(Long userId, Long tripId, Long waypointId, UpdateWaypointDTO updateWaypointDTO){
        log.debug("Updating waypoint: userId={}, tripId={}, waypointId={}, dto={}",
                userId, tripId, waypointId, updateWaypointDTO);

        WaypointEntity waypointEntity = waypointRepository.findByIdAndTripId(waypointId,tripId);
        if (waypointEntity == null){
            throw new RuntimeException("Waypoint not found.");
        }
        if (!waypointEntity.getUserId().equals(userId)){
            throw new RuntimeException("Access denied.");
        }
        
        boolean datesUpdated = false;
        
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
        if (updateWaypointDTO.getStartDate() != null) {
            waypointEntity.setStartDate(updateWaypointDTO.getStartDate());
            datesUpdated = true;
        }
        if (updateWaypointDTO.getEndDate() != null) {
            waypointEntity.setEndDate(updateWaypointDTO.getEndDate());
            datesUpdated = true;
        }

        waypointRepository.save(waypointEntity);
        WaypointDTO updatedWaypoint = new WaypointDTO(waypointEntity);
        
        // If start and end dates are both set and have been updated, fetch and update weather data
        if (datesUpdated && waypointEntity.getStartDate() != null && waypointEntity.getEndDate() != null) {
            try {
                log.info("Dates updated for waypoint {}. Updating weather data.", waypointId);
                WeatherDTO weatherDTO = weatherService.getWeatherForWaypoint(userId, tripId, waypointId);
                updatedWaypoint.setWeather(weatherDTO);
                log.info("Weather data updated successfully for waypoint {}", waypointId);
            } catch (Exception e) {
                log.error("Failed to update weather data for waypoint {}: {}", waypointId, e.getMessage());
                // We don't want to fail the entire update if weather update fails
                // Just log the error and continue
            }
        }
        
        return updatedWaypoint;
    }


}
