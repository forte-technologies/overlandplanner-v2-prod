package dev.forte.overlandplannerv2.trip;

import dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<TripEntity, Long> {

    List<TripEntity> findByUserId(Long userId);
    TripEntity findByName(String name);

    TripEntity findByIdAndUserId(Long tripId, Long userId);

    @Query("SELECT new dev.forte.overlandplannerv2.trip.dtos.SimpleTripDTO(t.id, t.userId, t.name) " +
            "FROM trips t WHERE t.userId = :userId")
    List<SimpleTripDTO> findSimpleTripsByUserId(@Param("userId") Long userId);

}

