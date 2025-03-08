package dev.forte.overlandplannerv2.weather;

import dev.forte.overlandplannerv2.waypoint.WaypointEntity;
import dev.forte.overlandplannerv2.waypoint.WaypointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeatherUpdateService {

    private final WeatherRepository weatherRepository;
    private final WaypointRepository waypointRepository;

    public WeatherUpdateService(WeatherRepository weatherRepository, WaypointRepository waypointRepository) {
        this.weatherRepository = weatherRepository;
        this.waypointRepository = waypointRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrCreateWeather(Long waypointId, Double minTemp, Double maxTemp) {
        WaypointEntity waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new RuntimeException("Waypoint not found"));

        WeatherEntity weather = weatherRepository.findByWaypointId(waypointId);
        if (weather != null) {
            weather.setAvgMinTemperature(minTemp);
            weather.setAvgMaxTemperature(maxTemp);
            weatherRepository.save(weather);
        } else {
            weather = new WeatherEntity();
            weather.setWaypoint(waypoint);
            weather.setAvgMinTemperature(minTemp);
            weather.setAvgMaxTemperature(maxTemp);
            waypoint.setWeather(weather);
            waypointRepository.save(waypoint);
        }
    }
}