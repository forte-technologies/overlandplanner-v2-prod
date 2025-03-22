package dev.forte.overlandplannerv2.trip.dtos;

import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;

import java.time.LocalDate;
import java.util.List;


public class CreateTripDTO {

    public String name;
    public String description;
    private List<CreateWaypointDTO> waypointDTOs;
    private LocalDate startDate;
    private LocalDate endDate;

    public CreateTripDTO() {}


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

    public List<CreateWaypointDTO> getWaypointDTOs() {
        return waypointDTOs;
    }

    public void setWaypointDTOs(List<CreateWaypointDTO> waypointDTOs) {
        this.waypointDTOs = waypointDTOs;
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
