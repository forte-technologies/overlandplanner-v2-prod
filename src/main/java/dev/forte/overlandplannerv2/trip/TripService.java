package dev.forte.overlandplannerv2.trip;


import dev.forte.overlandplannerv2.error.TripNotFoundException;
import dev.forte.overlandplannerv2.trip.dtos.CreateTripDTO;
import dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO;
import dev.forte.overlandplannerv2.trip.dtos.TripDTO;
import dev.forte.overlandplannerv2.trip.dtos.UpdateTripDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TripService {
    
    private final TripRepository tripRepository;
    
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }
    
    public void addTrip(CreateTripDTO tripDTO, Long userId){
        
        TripEntity tripEntity = new TripEntity();
        tripEntity.setUserId(userId);
        tripEntity.setDescription(tripDTO.getDescription());
        tripEntity.setName(tripDTO.getName());
        
        tripRepository.save(tripEntity);
    }
    
    public List<TripDTO> getTrips(Long userId){

        List<TripEntity> trips = tripRepository.findByUserId(userId);
        if (trips.isEmpty()){
            log.info("No trips found for user: {}", userId);
        } else {
            log.info("Found {} trips for user: {}", trips.size(), userId);
        }
        return trips.stream().map(TripDTO::new).toList();
    }
    
    public TripDTO getTripByUser(Long userId, Long tripId){

        log.debug("Fetching trip for user ID: {} and trip ID: {}", userId, tripId);
        TripEntity trip = tripRepository.findByIdAndUserId(tripId,userId);
        if (trip == null){
            log.error("No trip found for user ID: {} and trip ID: {}", userId, tripId);
            throw new TripNotFoundException("Trip not found.");
        }
        log.debug("Found trip: {}", trip);
        return new TripDTO(trip);
    }

    public TripDTO updateTripByUser(Long userId, Long tripId, UpdateTripDTO updateTripDTO) {

        TripEntity trip = tripRepository.findByIdAndUserId(tripId, userId);
        if (trip == null) {
            throw new TripNotFoundException("Trip not found.");
        }

        if (updateTripDTO.getName() != null) {
            trip.setName(updateTripDTO.getName());
        }
        if (updateTripDTO.getDescription() != null) {
            trip.setDescription(updateTripDTO.getDescription());
        }
        tripRepository.save(trip);
        log.info("Updated trip for user ID: {} and trip ID: {}", userId, tripId);
        return new TripDTO(trip);
    }

    public void deleteTripByUser(Long userId, Long tripId) {

        TripEntity trip = tripRepository.findByIdAndUserId(tripId, userId);
        if (trip == null) {
            throw new TripNotFoundException("Trip not found.");
        }
        tripRepository.delete(trip);
        log.info("Deleted trip for user ID: {} and trip ID: {}", userId, tripId);
    }

    public List<SimpleTripDTO> getSimpleTripsByUser(Long userId) {
        return tripRepository.findSimpleTripsByUserId(userId);
    }


}
