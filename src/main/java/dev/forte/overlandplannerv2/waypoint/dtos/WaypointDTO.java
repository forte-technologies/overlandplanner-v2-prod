package dev.forte.overlandplannerv2.waypoint.dtos;

import dev.forte.overlandplannerv2.waypoint.WaypointEntity;

public class WaypointDTO {

    private Long id;
    private Long tripId;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;


    public WaypointDTO(WaypointEntity entity) {
        this.id = entity.getId();
        this.tripId = entity.getTrip() != null ? entity.getTrip().getId() : null; // Map trip ID safely
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
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
}
