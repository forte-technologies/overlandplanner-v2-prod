package dev.forte.overlandplannerv2.waypoint.dtos;

public class SimpleWaypointDTO {

    private Long id;
    private Long userId;
    private Long tripId;
    private String name;
    private Double latitude;
    private Double longitude;


    public SimpleWaypointDTO(Long id, Long userId, Long tripId, String name, Double latitude , Double longitude) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.name = name;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
