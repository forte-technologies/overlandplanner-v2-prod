package dev.forte.overlandplannerv2.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/trips/{tripId}/waypoints/{waypointId}/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherDTO> getWeather(
            Authentication authentication,
            @PathVariable Long tripId,
            @PathVariable Long waypointId) {

        Long userId = getAuthenticatedUserId(authentication);
        WeatherDTO weather = weatherService.getWeatherForWaypoint(userId, tripId, waypointId);
        return ResponseEntity.ok(weather);
    }
}