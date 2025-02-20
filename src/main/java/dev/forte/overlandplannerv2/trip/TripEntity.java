package dev.forte.overlandplannerv2.trip;


import dev.forte.overlandplannerv2.waypoint.WaypointEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "trips")
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id") // Align table column name
    private Long id;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Initialize with default value

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaypointEntity> waypoints;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<WaypointEntity> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WaypointEntity> waypoints) {
        this.waypoints = waypoints;
    }
}
