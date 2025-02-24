package dev.forte.overlandplannerv2.waypoint;


import dev.forte.overlandplannerv2.trip.TripEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "waypoints")
public class WaypointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waypoint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many waypoints belong to one trip (lazy loading)
    @JoinColumn(name = "trip_id", nullable = false) // Foreign key column in the database
    private TripEntity trip; // Relational mapping to TripEntity

    @Column(name = "user_id", nullable = false) // Still store user ID directly
    private Long userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = true) // Optional field: description
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;


    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TripEntity getTrip() {
        return trip;
    }

    public void setTrip(TripEntity trip) {
        this.trip = trip;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
