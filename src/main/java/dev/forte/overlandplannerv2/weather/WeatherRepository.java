package dev.forte.overlandplannerv2.weather;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {

    @Modifying
    @Transactional
    void deleteByWaypointId(Long waypointId);

    boolean existsByWaypointId(Long waypointId);

    WeatherEntity findByWaypointId(Long waypointId);

    @Modifying
    @Query("UPDATE weather w SET w.avgMinTemperature = :minTemp, w.avgMaxTemperature = :maxTemp WHERE w.waypoint.id = :waypointId")
    void updateWeatherForWaypoint(Long waypointId, Double minTemp, Double maxTemp);


}
