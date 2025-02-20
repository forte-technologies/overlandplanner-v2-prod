package dev.forte.overlandplannerv2.trip.dtos;

public class UpdateTripDTO {

    private String name;
    private String description;

    public UpdateTripDTO() {}

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
}