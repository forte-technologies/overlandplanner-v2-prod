package dev.forte.overlandplannerv2.trip;

import dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<TripEntity, Long> {

    List<TripEntity> findByUserId(Long userId);
    TripEntity findByName(String name);

    TripEntity findByIdAndUserId(Long tripId, Long userId);
    
    // New method for pagination
    Page<TripEntity> findByUserId(Long userId, Pageable pageable);

    TripEntity findByUserIdAndName(Long userId, String name);


    @Query("SELECT t FROM trips t WHERE t.userId = :userId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TripEntity> findByUserIdAndNameContainingIgnoreCase(@Param("userId") Long userId, @Param("query") String query);


    @Query("SELECT new dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO(t.id, t.userId, t.name) " +
            "FROM trips t WHERE t.userId = :userId")
    List<SimpleTripDTO> findSimpleTripsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM trips t WHERE t.userId = :userId ORDER BY t.id DESC LIMIT 1")
    TripEntity findLatestTripByUserId(@Param("userId") Long userId);


}

