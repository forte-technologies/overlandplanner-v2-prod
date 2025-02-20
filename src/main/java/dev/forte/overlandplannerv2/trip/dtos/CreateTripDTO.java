package dev.forte.overlandplannerv2.trip.dtos;

import dev.forte.overlandplannerv2.waypoint.dtos.CreateWaypointDTO;

import java.util.List;


public class CreateTripDTO {

    public String name;
    public String description;
    private List<CreateWaypointDTO> waypointDTOs;

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
}
