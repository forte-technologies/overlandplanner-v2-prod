package dev.forte.overlandplannerv2.trip.dtos;

import dev.forte.overlandplannerv2.trip.TripEntity;
import dev.forte.overlandplannerv2.waypoint.dtos.WaypointDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TripDTO {
    private int waypointCount;

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<WaypointDTO> waypointDTOS;

    public TripDTO(TripEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.createdAt = entity.getCreatedAt(); // Map LocalDateTime here
        this.waypointDTOS = entity.getWaypoints() != null
                ? entity.getWaypoints().stream().map(WaypointDTO::new).collect(Collectors.toList())
                : null;
        this.waypointCount = (entity.getWaypoints() != null) ? entity.getWaypoints().size() : 0;

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

    public List<WaypointDTO> getWaypointDTOS() {
        return waypointDTOS;
    }

    public void setWaypointDTOS(List<WaypointDTO> waypointDTOS) {
        this.waypointDTOS = waypointDTOS;
    }

    public int getWaypointCount() {
        return waypointCount;
    }

    public void setWaypointCount(int waypointCount) {
        this.waypointCount = waypointCount;
    }

}
