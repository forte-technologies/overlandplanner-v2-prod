package dev.forte.overlandplannerv2.waypoint.dtos;

import dev.forte.overlandplannerv2.waypoint.WaypointEntity;
import dev.forte.overlandplannerv2.weather.WeatherDTO;

import java.time.LocalDate;

public class WaypointDTO {
    private Long id;
    private Long tripId;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private LocalDate startDate;
    private LocalDate endDate;
    private WeatherDTO weather;

    public WaypointDTO(WaypointEntity entity) {
        if (entity == null) {
            return; // Handle null entity gracefully
        }
        this.id = entity.getId();
        this.tripId = entity.getTrip() != null ? entity.getTrip().getId() : null;
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();

        if (entity.getWeather() != null) {
            try {
                this.weather = new WeatherDTO(
                    entity.getWeather().getId(),
                    entity.getId(),
                    entity.getWeather().getAvgMinTemperature(),
                    entity.getWeather().getAvgMaxTemperature(),
                    entity.getWeather().getTemperatureUnit()
                );
            } catch (Exception e) {
                // Log but don't throw the exception - just leave weather as null
                System.out.println("Error mapping weather data: " + e.getMessage());
            }
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public WeatherDTO getWeather() {
        return weather;
    }

    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
    }
}
